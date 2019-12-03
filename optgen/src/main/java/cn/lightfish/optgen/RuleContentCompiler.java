package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.MessageFormat;
import java.util.List;

public class RuleContentCompiler {
    RuleCompiler complier;
    SourceLoc src;
    boolean matchPattern = false;
    boolean customFunc = false;

    public RuleContentCompiler(RuleCompiler compiler, SourceLoc source, boolean matchPattern) {
        this.complier = compiler;
        this.src = source;
        this.matchPattern = matchPattern;
    }


    public void addErr(Expr loc, String err) {
        SourceLoc source = loc.source();
        if (source == null) {
            source = this.src;
        }
        this.complier.compiler.addErr(source, err);
    }

    public List<String> errors() {
        return  this.complier.compiler.errors;
    }

    @Data
    class CompileOpNameRes {
        Expr fn;
        boolean ok;

        public CompileOpNameRes(Expr fn, boolean b) {

            this.fn = fn;
            this.ok = b;
        }
    }

    public Expr compile(Expr e) {
        switch (e.op()) {
            case FuncOp:
                return compileFunc((FuncExpr) e);
            case BindOp:
                return complieBind((BindExpr) e);
            case RefOp: {
                if (matchPattern && !customFunc) {
                    this.addDisallowedErr(e, "cannot use variable references");
                } else {
                    RefExpr e1 = (RefExpr) e;
                    if (!this.complier.bindings.containsKey(e1.getLabel())) {
                        this.addErr(e, String.format("unrecognized variable name '%s'", e1.getLabel()));
                    }
                }
                break;
            }
            case ListOp: {
                if (matchPattern && customFunc) {
                    this.addDisallowedErr(e, "cannot use lists");
                } else {
                    this.compileList((ListExpr) e);
                }
                break;
            }
            case NotOp:
            case AndOp:{
                if (!matchPattern && customFunc) {
                    this.addDisallowedErr(e, "cannot use boolean expressions");
                }
                break;
            }
            case NameOp:{
                NameExpr t = (NameExpr) e;
                if (matchPattern && !customFunc) {
                    this.addDisallowedErr(e, "cannot use boolean expressions");
                }else{
                    DefineExpr define = this.complier.compiled.lookupDefine(t.value());
                    if (define == null){
                        addErr(t, MessageFormat.format("%s is not an operator name",t));
                    }
                }
                break;
            }
            case AnyOp:{
                if (!matchPattern||customFunc){
                    addDisallowedErr(e, "cannot use wildcard matcher");
                }
                break;
            }
        }
        return e.visit(new VisitFunc() {
            @Override
            public Expr apply(Expr e) {
                return RuleContentCompiler.this.compile(e);
            }
        });
    }


    private Expr complieBind(BindExpr bind) {
        DataType dataType = this.complier.bindings.get(bind.getLabel());
        if (dataType!=null){
            addErr(bind,String.format("duplicate bind label '%s'",bind.getLabel()));
        }

        this.complier.bindings.put(bind.getLabel(),DataType.AnyDataType);
        return bind.visit(new VisitFunc() {
            @Override
            public Expr apply(Expr e) {
                return RuleContentCompiler.this.compile(e);
            }
        });
    }

    private void compileList(ListExpr e) {
        boolean foundNotAny = false;
        int count = e.childCount();
        for (int i = 0; i < count; i++) {
            Expr item = e.child(i);
            if (item.op() == Operator.ListAnyOp){
                if (!matchPattern){
                    addErr(e,"list constructor cannot use '...'");
                }
            }else{
                if (matchPattern&&foundNotAny){
                    addErr(item,"list matcher cannot contain multiple expressions");
                    break;
                }
                foundNotAny = true;
            }
        }
    }
    private Expr compileFunc(FuncExpr fn) {
        RuleContentCompiler nestd = new RuleContentCompiler(this.complier,fn.source(),this.matchPattern);

        Expr funcName = fn.getName();

        if (funcName instanceof FuncExpr){
            if (matchPattern){
                addErr(fn,("cannot match dynamic name"));
            }
            funcName = compileFunc((FuncExpr)funcName);
        }else {
            CheckNamesRes checkNamesRes = checkNames(fn);
            if (!checkNamesRes.ok){
                return null;
            }
            NamesExpr names = checkNamesRes.names;

            if (names.childCount()==1){
                 funcName = names.child(0);
            }else {
                funcName = names;
            }

            int count = names.childCount();
            DefineExpr prototype = null;
            for (int i = 0; i < count; i++) {
                NameExpr name = names.child(i);
                DefineSetExpr defines = this.complier.compiled.lookupMatchDefines(name.value());
                if (defines!=null){

                    int count1 = defines.childCount();
                    for (int j = 0; j < count1; j++) {
                        DefineExpr define = defines.child(j);
                        if (define.getFields().childCount()<fn.getArgs().childCount()){
                            addErr(fn,String.format("%s has only %d fields",define.getName().value(),define.getFields().childCount()));
                            continue;
                        }
                        if (prototype == null){
                            prototype = define;
                            continue;
                        }
                        SliceExpr args = fn.getArgs();
                        int count2 = args.childCount();
                        for (int k = 0; k < count2; k++) {
                            DefineFieldExpr child = (DefineFieldExpr) define.getFields().child(k);
                            DefineFieldExpr pDefineFieldExpr = (DefineFieldExpr) prototype.getFields().child(k);
                            if(!child.getType().equals(pDefineFieldExpr.getType())){
                                addErr(fn,String.format("%s and %s fields do not have same types",define.getName().value(),
                                        prototype.getName().value()));
                            }
                        }
                    }
                }else {
                    if(names.childCount()!=1){
                        addErr(fn,"custom function cannot have multiple names");
                        return fn;
                    }
                    if ("OpName".equals(name.value())){
                        CompileOpNameRes compileOpNameRes = this.compileOpName(fn);
                        if(compileOpNameRes.ok){
                            return compileOpNameRes.fn;
                        }
                    }
                    nestd.customFunc = true;
                }
            }
        }
        if (matchPattern&&customFunc&&!nestd.customFunc){
            addErr(fn,"custom function name cannot be an operator name");
            return fn;
        }
        SliceExpr args = (SliceExpr)fn.getArgs().visit(e -> {
            return nestd.compile(e);
        });
        if (nestd.customFunc){
            return new CustomFuncExpr(funcName,args,fn.source());
        }
        return new FuncExpr(fn.source(),funcName,args);
    }

    @AllArgsConstructor
    class CheckNamesRes{
        NamesExpr names;
        boolean ok;
    }

    private CheckNamesRes checkNames(FuncExpr fn) {
        Expr expr = fn.getName();
        NamesExpr names;
        if ( expr instanceof NamesExpr){
            names =(NamesExpr) expr;
        }else if (expr instanceof NameExpr){
            names = new NamesExpr();
            names.append((NameExpr)expr);
        }else {
            return new CheckNamesRes(new NamesExpr(),false);
        }
        if (!matchPattern){
            if (names.childCount()!=1){
                addErr(fn,"constructor cannot have multiple names");
                return new CheckNamesRes(new NamesExpr(),false);
            }

            DefineSetExpr defines = this.complier.compiled.lookupMatchDefines(names.child(0).value());
            if (defines==null||defines.childCount()==0){
                return new CheckNamesRes(names,true);
            }

            DefineExpr define = complier.compiled.lookupDefine(names.child(0).value());
            if (define==null){
                addErr(fn,"construct name cannot be a tag");
                return new CheckNamesRes(new NamesExpr(),false);
            }
        }
        return new CheckNamesRes(names,true);
    }

    public CompileOpNameRes compileOpName(FuncExpr fn) {
        if (fn.getArgs().childCount() > 1) {
            this.addErr(fn,"too many arguments to OpName function");
            return new CompileOpNameRes(fn,false);
        }
        if (fn.getArgs().childCount()==0){
            NameExpr opName = complier.opName;
            return new CompileOpNameRes(new NameExpr(opName.value()),true);
        }
        Expr child =(RefExpr) fn.getArgs().child(0);
        if (!(child instanceof RefExpr)){
            addErr(fn,("invalid OpName argument: argument must be a variable reference"));
            return new CompileOpNameRes(fn,false);
        }
        return new CompileOpNameRes(fn,false);
    }

    public void addDisallowedErr(Expr loc, String disallowed) {
        if (this.matchPattern) {
            if (this.customFunc) {
                this.addErr(loc, String.format("custom match function %s", disallowed));
            } else {
                this.addErr(loc, String.format("match pattern %s", disallowed));
            }
        } else {
            if (this.customFunc) {
                this.addErr(loc, String.format("custom replace function %s", disallowed));
            } else {
                this.addErr(loc, String.format("replace pattern %s", disallowed));
            }
        }
    }
}
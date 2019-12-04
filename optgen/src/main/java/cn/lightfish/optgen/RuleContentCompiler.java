package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.MessageFormat;
import java.util.List;

/**
 * ruleContentCompiler is the workhorse of rule compilation. It is recursively
 * constructed on the stack in order to keep scoping context for match and
 * construct expressions. Semantics can change depending on the context.
 */
public class RuleContentCompiler {
    RuleCompiler complier;

    // src is the source location of the nearest match or construct expression,
    // and is used when the source location isn't otherwise available.
    SourceLoc src;

    // matchPattern is true when compiling in the scope of a match pattern, and
    // false when compiling in the scope of a replace pattern.
    boolean matchPattern = false;

    // customFunc is true when compiling in the scope of a custom match or
    // replace function, and false when compiling in the scope of an op matcher
    // or op constructor.
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
        // Recurse into match or construct operator separately, since they will need
        // to ceate new context before visiting arguments.
        switch (e.op()) {
            case FuncOp:
                return compileFunc((FuncExpr) e);
            case BindOp:
                return compileBind((BindExpr) e);
            case RefOp: {
                if (matchPattern && !customFunc) {
                    this.addDisallowedErr(e, "cannot use variable references");
                } else {
                    // Check that referenced variable exists.
                    RefExpr e1 = (RefExpr) e;
                    if (!this.complier.bindings.containsKey(e1.getLabel())) {
                        this.addErr(e, String.format("unrecognized variable name '%s'", e1.getLabel().value()));
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
                if (!matchPattern || customFunc) {
                    this.addDisallowedErr(e, "cannot use boolean expressions");
                }
                break;
            }
            case NameOp:{
                NameExpr t = (NameExpr) e;
                if (matchPattern && !customFunc) {
                    this.addErr(e, String.format("cannot match literal name '%s'",t.value()));
                }else{
                    DefineExpr define = this.complier.compiled.lookupDefine(t.value());
                    if (define == null){
                        addErr(t, String.format("%s is not an operator name",t.value()));
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
        // Pre-order traversal.
        return e.visit(new VisitFunc() {
            @Override
            public Expr apply(Expr e) {
                return RuleContentCompiler.this.compile(e);
            }
        });
    }


    private Expr compileBind(BindExpr bind) {
        // Ensure that binding labels are unique.
        DataType dataType = this.complier.bindings.get(bind.getLabel());
        if (dataType!=null){
            addErr(bind,String.format("duplicate bind label '%s'",bind.getLabel().value()));
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

        Expr items = e.child(0);
        int count = items.childCount();
        for (int i = 0; i < count; i++) {
            Expr item = items.child(i);
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
        RuleContentCompiler nested = new RuleContentCompiler(this.complier,fn.source(),this.matchPattern);

        Expr funcName = fn.getName();

        if (funcName instanceof FuncExpr){
            // Function name is itself a function that dynamically determines name.
            if (matchPattern){
                addErr(fn,("cannot match dynamic name"));
            }
            funcName = compileFunc((FuncExpr)funcName);
        }else {
            // Ensure that all function names are defined and check whether this is a
            // custom match function invocation.
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
                DefineSetExpr defines = this.complier.compiled.lookupMatchingDefines(name.value());
                if (defines!=null){
                    // Ensure that each operator has at least as many operands as the
                    // given function has arguments. The types of those arguments must
                    // be the same across all the operators.
                    int count1 = defines.childCount();
                    for (int j = 0; j < count1; j++) {
                        DefineExpr define = defines.child(j);
                        if (define.getFields().childCount()<fn.getArgs().childCount()){
                            addErr(fn,String.format("%s has only %d fields",define.getName().value(),define.getFields().childCount()));
                            continue;
                        }
                        if (prototype == null){
                            // Save the first define in order to compare it against all
                            // others.
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
                    // This must be an invocation of a custom function, because there is
                    // no matching define.
                    if(names.childCount()!=1){
                        addErr(fn,"custom function cannot have multiple names");
                        return fn;
                    }
                    // Handle built-in functions.
                    if ("OpName".equals(name.value())){
                        CompileOpNameRes compileOpNameRes = this.compileOpName(fn);
                        if(compileOpNameRes.ok){
                            return compileOpNameRes.fn;
                        }
                        // Fall through and create OpName as a CustomFuncExpr. It may
                        // be rewritten during type inference if it can be proved it
                        // always constructs a single operator.
                    }
                    nested.customFunc = true;
                }
            }
        }
        if (this.matchPattern&&this.customFunc&&!nested.customFunc){
            addErr(fn,"custom function name cannot be an operator name");
            return fn;
        }
        SliceExpr args = (SliceExpr)fn.getArgs().visit(e -> {
            return nested.compile(e);
        });
        if (nested.customFunc){
            return new CustomFuncExpr(funcName,args,fn.source());
        }
//        assert funcName instanceof NamesExpr || funcName instanceof NameExpr;
        return new FuncExpr(fn.source(),funcName,args);
    }

    @AllArgsConstructor
    class CheckNamesRes{
        NamesExpr names;
        boolean ok;
    }

    // checkNames ensures that all function names are valid operator names or tag
// names, and that they are legal in the current context. checkNames returns
// the list of names as a NameExpr, as well as a boolean indicating whether they
// passed all validity checks.
    private CheckNamesRes checkNames(FuncExpr fn) {
        Expr expr = fn.getName();
        NamesExpr names;
        if ( expr instanceof NamesExpr){
            names =(NamesExpr) expr;
        }else if (expr instanceof NameExpr){
            names = new NamesExpr();
            names.append((NameExpr)expr);
        }else {
            // Name dynamically derived by function.
            return new CheckNamesRes(new NamesExpr(),false);
        }
        // Don't allow replace pattern to have multiple names or a tag name.
        if (!matchPattern){
            if (names.childCount()!=1){
                addErr(fn,"constructor cannot have multiple names");
                return new CheckNamesRes(new NamesExpr(),false);
            }

            DefineSetExpr defines = this.complier.compiled.lookupMatchingDefines(names.child(0).value());
            if (defines==null||defines.childCount()==0){
                // Must be custom function name.
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
            // No args to OpName function refers to top-level match operator.
            NameExpr opName = this.complier.opName;
            return new CompileOpNameRes(new NameExpr(opName.value()),true);
        }
        Expr child = fn.getArgs().child(0);
        // Otherwise expect a single variable reference argument.
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
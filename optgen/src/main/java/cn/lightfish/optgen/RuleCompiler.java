package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import lombok.Data;

import java.util.*;

import static cn.lightfish.optgen.DataType.*;

@Data
public class RuleCompiler {
    Compiler compiler;
    CompiledExpr compiled;
    RuleExpr rule;

    Map<StringExpr, DataType> bindings;

    NameExpr opName;

    public void compile(Compiler compiler, RuleExpr rule) {
        this.compiler = compiler;
        this.compiled = compiler.compiled;
        this.rule = rule;

        Expr name = rule.getMatch().getName();

        if (name instanceof FuncExpr) {
            this.compiler.addErr(rule.getMatch().source(), "cannot match dynamic name");
            return;
        }

        NamesExpr namesExpr = rule.getMatch().nameChoice();
        int count = namesExpr.childCount();
        for (int i = 0; i < count; i++) {
            NameExpr child = namesExpr.child(i);
            DefineSetExpr defineSetExpr = compiled.lookupMatchDefines(child.value());
            if (defineSetExpr.getSet().isEmpty()) {
                defineSetExpr = null;
                this.compiler.addErr(rule.getMatch().source(), String.format("unrecognized match name '%s'", name));
            }
            Objects.requireNonNull(defineSetExpr);
            List<DefineExpr> set = defineSetExpr.getSet();
            Objects.requireNonNull(set);
            for (DefineExpr defineExpr :set ) {
                this.expandRule(new NameExpr(defineExpr.getName().value()));
            }

        }
    }

    public void expandRule(NameExpr opName) {
        int errCntBefore = this.compiler.errors.size();
        this.opName = opName;

        this.bindings = new HashMap<>();

        NamesExpr namesExpr = new NamesExpr();
        namesExpr.append(new NameExpr(opName.value()));
        FuncExpr match = new FuncExpr(this.rule.getMatch().source(), namesExpr);

        SliceExpr args = this.rule.getMatch().getArgs();
        int count = args.childCount();
        for (int i = 0; i < count; i++) {
            match.append(args.child(i));
        }

        RuleContentCompiler compiler;

        compiler = new RuleContentCompiler(this, this.rule.source(), true);
        match = (FuncExpr) compiler.compile(match);

        compiler = new RuleContentCompiler(this, this.rule.source(), false);
        FuncExpr replace = (FuncExpr) compiler.compile(rule.getReplace());

        RuleExpr ruleExpr = new RuleExpr(rule.getSourceLoc(),
                this.rule.getName(),
                this.rule.getComments(),
                this.rule.getTags(),
                match,
                replace
        );
        if (errCntBefore == this.compiler.errors.size()) {
            inferTypes(ruleExpr.getMatch(), AnyDataType);
            inferTypes(ruleExpr.getReplace(), AnyDataType);
        }
        try {
            this.compiled.rules.append(ruleExpr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inferTypes(Expr e, DataType suggested) {
        Operator op = e.op();
        switch (op) {
            case FuncOp: {
                DefineSetExpr defineSetExpr;
                FuncExpr funcExpr = (FuncExpr) e;
                if (funcExpr.hasDynamicName()) {
                    Expr name = funcExpr.getName();
                    boolean ok = name instanceof CustomFuncExpr;
                    CustomFuncExpr customFuncExpr = (CustomFuncExpr) name;

                    if (!ok || !"OpName".equals(customFuncExpr.getName().value())) {
                        panic(String.format("%s not allowed as dynamic function name", funcExpr.getName().value()));
                    }
                    StringExpr label = ((RefExpr) funcExpr.getArgs().child(0)).getLabel();
                    DataType type = this.bindings.get(label);
                    if (type == null) {
                        panic(String.format("$%s does not have its type set", label.value()));
                    }

                    ok = type instanceof DefineSetExpr;
                    if (!ok) {
                        this.compiler.addErr(customFuncExpr.getArgs().child(0).source(),
                                "cannot infer type of construction expression"
                        );
                        break;
                    }
                    defineSetExpr = (DefineSetExpr) type;

                    if (defineSetExpr.childCount() == 1) {
                        DefineExpr child = defineSetExpr.child(0);
                        funcExpr.setName(new NameExpr(child.getName().value()));
                    }
                    assert defineSetExpr.childCount() > 0;
                } else {
                    NamesExpr names = funcExpr.nameChoice();
                    defineSetExpr = new DefineSetExpr();
                    int count = names.childCount();
                    assert count > 0;
                    for (int i = 0; i < count; i++) {
                        NameExpr child = names.child(i);
                        List<DefineExpr> set = Collections.emptyList();
                        try {
                            DefineSetExpr defineSetExpr1 = this.compiled.lookupMatchDefines(child.value());
                            if (defineSetExpr1==null){
                                continue;
                            }
                            set =defineSetExpr1. getSet();
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }
                        assert !set.isEmpty();
                        if (set!=null) {
                            defineSetExpr.getSet().addAll(set);
                        }
                    }
                    funcExpr.setType(new DefineSetDataType(defineSetExpr));

                    if( defineSetExpr.childCount() == 0){
                        System.out.println();
                    }
                }
                DefineExpr prototype = null;
                try {
                    prototype = defineSetExpr.child(0);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                inferTypes(funcExpr.getName(), AnyDataType);

                SliceExpr sliceExpr = funcExpr.getArgs();
                int count = sliceExpr.childCount();
                for (int i = 0; i < count; i++) {
                    Expr arg = sliceExpr.child(i);
                    DefineFieldExpr child = (DefineFieldExpr) prototype.getFields().child(i);
                    StringExpr type = child.getType();
                    ExternalDataType suggest = new ExternalDataType(new NameExpr(type.value()));
                    this.inferTypes(arg, suggest);
                }

                break;
            }
            case CustomFuncOp:{
                CustomFuncExpr e1 = (CustomFuncExpr) e;
                e1.setType(suggested);
                SliceExpr args = e1.getArgs();
                int count = args.childCount();
                for (int i = 0; i < count; i++) {
                    inferTypes(args.child(i),AnyDataType);
                }
                break;
            }
            case AndOp: {
                AndExpr andExpr = (AndExpr) e;
                inferTypes(andExpr.getLeft(), suggested);
                inferTypes(andExpr.getRight(), suggested);
                if (doTypesContradict(andExpr.getLeft().inferredType(), andExpr.getRight().inferredType())) {
                    this.compiler.addErr(andExpr.source(), "match patterns contradict one another; both cannot match");
                }
                andExpr.setType(mostRestrictiveDataType(andExpr.getLeft().inferredType(), andExpr.getRight().inferredType()));
                break;
            }
            case NotOp: {
                NotExpr notExpr = (NotExpr) e;
                this.inferTypes(notExpr.getInput(), suggested);
                notExpr.setType(suggested);
                break;
            }
            case ListOp: {
                ListExpr t = (ListExpr) e;
                DataType type = mostRestrictiveDataType(ListDataType, suggested);
                int count = t.childCount();
                for (int i = 0; i < count; i++) {
                    Expr child = t.child(i);
                    inferTypes(child, AnyDataType);
                }
                break;
            }
            case BindOp: {
                BindExpr bindExpr = (BindExpr) e;
                this.inferTypes(bindExpr.getTarget(), suggested);
                bindExpr.setType(bindExpr.getTarget().inferredType());
                this.bindings.put(bindExpr.getLabel(), bindExpr.getType());
                break;
            }
            case RefOp: {
                RefExpr refExpr = (RefExpr) e;
                DataType dataType = this.bindings.get(refExpr.getLabel());
                if (dataType == null) {
                    panic(String.format("$%s does not have its type set", refExpr.getLabel()));
                }
                refExpr.setType(mostRestrictiveDataType(dataType, suggested));
                break;
            }
            case AnyOp: {
                AnyExpr anyExpr = (AnyExpr) e;
                anyExpr.setType(suggested);
                break;
            }
            case StringOp:
            case NumberOp:
            case ListAnyOp:
            case NameOp:
            case NamesOp:
                return;
            default:
                panic(String.format("unhandled expression: %s", e));
        }
    }

    private DataType mostRestrictiveDataType(DataType left, DataType right) {
        if (isTypeMoreRestrictive(right, left)) {
            return right;
        }
        return left;
    }
}
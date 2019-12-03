package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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
            if(defineSetExpr.getSet().isEmpty()){
                defineSetExpr = null;
                this.compiler.addErr(rule.getMatch().source(),String.format("unrecognized match name '%s'",name));
            }
            for (DefineExpr defineExpr : defineSetExpr.getSet()) {
                this.expandRule(new NameExpr( defineExpr.getName().value()));
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
        match.append(match.getArgs());

        RuleContentCompiler compiler;

        compiler = new RuleContentCompiler(this, this.rule.source(), true);
        match = (FuncExpr) compiler.compile(match);

        compiler = new RuleContentCompiler(this, this.rule.source(), false);
        compiler.compile(rule.getReplace());

        RuleExpr ruleExpr = new RuleExpr(rule.getSourceLoc(),
                this.rule.getName(),
                this.rule.getComments(),
                this.rule.getTags(),
                this.rule.getMatch(),
                this.rule.getReplace()
        );
        if (errCntBefore == this.compiler.errors.size()) {
            inferTypes(ruleExpr.getMatch(), AnyDataType);
            inferTypes(ruleExpr.getReplace(), AnyDataType);
        }
        this.compiled.rules.append(ruleExpr);

    }

    public void inferTypes(Expr e, DataType suggested) {
        Operator op = e.op();
        switch (op) {
            case FuncOp: {
                FuncExpr funcExpr = (FuncExpr) e;
                if (funcExpr.hasDynamicName()) {
                    Expr name = funcExpr.getName();
                    boolean ok = name instanceof CustomFuncExpr;
                    CustomFuncExpr customFuncExpr = (CustomFuncExpr) name;

                    if (!ok || "OpName".equals(customFuncExpr.getName().value())) {
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
                    DefineSetExpr type1 = (DefineSetExpr) type;

                    if (type1.childCount() == 1) {
                        DefineExpr child = type1.child(0);
                        funcExpr.setName(new NameExpr(child.getName().value()));
                    }
                } else {
                    NamesExpr names = funcExpr.nameChoice();
                    DefineSetExpr defineSetExpr = new DefineSetExpr();
                    int count = names.childCount();

                    for (int i = 0; i < count; i++) {
                        NameExpr child = names.child(i);
                        defineSetExpr.getSet().addAll(this.compiled.lookupMatchDefines(child.value()).getSet());
                    }
                    funcExpr.setType(new DefineSetDataType(defineSetExpr));
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
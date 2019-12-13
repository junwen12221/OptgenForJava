package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import lombok.Data;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static cn.lightfish.optgen.DataType.*;

/**
 * ruleCompiler compiles a single rule. It is a separate struct in order to
 * keep state for the ruleContentCompiler.
 */
@Data
public class RuleCompiler {
    Compiler compiler;
    CompiledExpr compiled;
    RuleExpr rule;


    // bindings tracks variable bindings in order to ensure uniqueness and to
    // infer types.
    Map<StringExpr, DataType> bindings;


    // opName keeps the root match name in order to compile the OpName built-in
    // function.
    NameExpr opName;

    public void compile(Compiler compiler, RuleExpr rule) {
        this.compiler = compiler;
        this.compiled = compiler.compiled;
        this.rule = rule;

        Expr name = rule.getMatch().getName();

        if (name instanceof FuncExpr) {
            // Function name is itself a function that dynamically determines name.
            this.compiler.addErr(rule.getMatch().source(), "cannot match dynamic name");
            return;
        }

        // Expand root rules that match multiple operators into a separate match
        // expression for each matching operator.
        NamesExpr namesExpr = rule.getMatch().nameChoice();
        int count = namesExpr.childCount();
        for (int i = 0; i < count; i++) {
            NameExpr child = namesExpr.child(i);
            DefineSetExpr defineSetExpr = compiled.lookupMatchingDefines(child.value());
            if (defineSetExpr == null||defineSetExpr.getSet().isEmpty()) {
                // No defines with that tag found, which is not allowed.
                defineSetExpr = null;
                this.compiler.addErr(rule.getMatch().source(),format("unrecognized match name '%s'", name.value()));
            }
            if (defineSetExpr!=null) {
                List<DefineExpr> set = defineSetExpr.getSet();
                Objects.requireNonNull(set);
                for (DefineExpr defineExpr : set) {
                    this.expandRule(new NameExpr(defineExpr.getName().value()));
                }
            }

        }
    }
    String format(String format,Object i){
        return   String.format(format, i == null?"Unknown":Objects.toString(i));
    }
    String format(String format,Object... args){
        if (args == null){
            return format(format,(Object) args);
        }
      return   String.format(format, Arrays.stream(args).map(i->i == null?"Unknown":Objects.toString(i)).collect(Collectors.toList()));
    }

    /**
     * expandRule rewrites the current rule to match the given opname rather than
     * a list of names or a tag name. This transformation makes it easier for the
     * code generator, since all rules will never match more than one op at the
     * top-level.
     * @param opName
     */
    public void expandRule(NameExpr opName) {
        // Remember current error count in order to detect whether ruleContentCompiler
        // adds additional errors.
        int errCntBefore = this.compiler.errors.size();

        // Remember the root opname in case it's needed to compile the OpName
        // built-in function.
        this.opName = new NameExpr(opName.value());

        this.bindings = new HashMap<>();


        // Construct new match expression that matches a single name.
        NamesExpr namesExpr = new NamesExpr();
        namesExpr.append(new NameExpr(opName.value()));
        FuncExpr match = new FuncExpr(this.rule.getMatch().source(), namesExpr, this.rule.getMatch().getArgs());

        RuleContentCompiler compiler;

        compiler = new RuleContentCompiler(this, this.rule.source(), true);
        match =(FuncExpr) compiler.compile( match);

        compiler = new RuleContentCompiler(this, this.rule.source(), false);
       // System.out.println("-------------------------------replace----------------------------------------");
        Expr replace = compiler.compile( rule.getReplace());

        RuleExpr ruleExpr = new RuleExpr(rule.getSourceLoc(),
                this.rule.getName(),
                this.rule.getComments(),
                this.rule.getTags(),
                match,
                replace
        );

        /*
         Infer data types for expressions within the match and replace patterns.
         Do this only if the rule triggered no errors.
         */
        if (errCntBefore == this.compiler.errors.size()) {
            inferTypes(ruleExpr.getMatch(), AnyDataType);
            inferTypes(ruleExpr.getReplace(), AnyDataType);
        }

            this.compiled.rules.append(ruleExpr);
    }

    /**
     * inferTypes walks the tree and annotates it with inferred data types. It
     * reports any typing errors it encounters. Each expression is annotated with
     * either its "bottom-up" type which it infers from its inputs, or else its
     * "top-down" type (the suggested argument), which is passed down from its
     * ancestor(s). Each operator has its own rules of which to use.
     * @param e
     * @param suggested
     */
    public void inferTypes(Expr e, DataType suggested) {
        Operator op = e.op();
        switch (op) {
            case FuncOp: {
                DefineSetDataType    defType;
                FuncExpr funcExpr = (FuncExpr) e;
                // Special-case the OpName built-in function.
                if (funcExpr.hasDynamicName()) {
                    Expr name = funcExpr.getName();
                    boolean ok = name instanceof CustomFuncExpr;
                    CustomFuncExpr customFuncExpr = (CustomFuncExpr) name;

                    if (!ok || !"OpName".equals(customFuncExpr.getName().value())) {
                        panic(String.format("%s not allowed as dynamic function name", funcExpr.getName().value()));
                    }
                    // Inherit type of the opname target.
                    StringExpr label = ((RefExpr) customFuncExpr.getArgs().child(0)).getLabel();
                    DataType type = this.bindings.get(label);
                    funcExpr.setType(type);
                    if (type == null) {
                        panic(String.format("$%s does not have its type set", label.value()));
                    }

                    ok = type instanceof DefineSetDataType;
                    if (!ok) {
                        this.compiler.addErr(customFuncExpr.getArgs().child(0).source(),
                                "cannot infer type of construction expression"
                        );
                        break;
                    }
                    defType  = (DefineSetDataType) type;
                    // If the OpName refers to a single operator, rewrite it as a simple
                    // static name.
                    if (defType.defines.childCount() == 1) {
                        DefineExpr child = defType.defines.child(0);
                        funcExpr.setName(new NameExpr(child.getName().value()));
                    }
                } else {
                    // Construct list of defines that can be matched.
                    NamesExpr names = funcExpr.nameChoice();
                    DefineSetExpr defines = new DefineSetExpr();
                    int count = names.childCount();
                    for (int i = 0; i < count; i++) {
                        defines.append(this.compiled.lookupMatchingDefines(names.child(i).value()));
                    }
                    defType = new DefineSetDataType(defines);
                    funcExpr.setType(defType);
                }
                // First define in list is considered the "prototype" that all others
                // match. The matching is checked in ruleContentCompiler.compileFunc.
                DefineExpr prototype = defType.defines.child(0);

                // Recurse on name and arguments.
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
            case CustomFuncOp: {
                // Return type of custom function isn't known, but might be inferred from
                // context in which it's used.
                CustomFuncExpr e1 = (CustomFuncExpr) e;
                e1.setType(suggested);
                SliceExpr args = e1.getArgs();
                int count = args.childCount();
                // Recurse on arguments, passing AnyDataType as suggested type, because
                // no information is known about their types.
                for (int i = 0; i < count; i++) {
                    inferTypes(args.child(i), AnyDataType);
                }
                break;
            }
            case BindOp: {
                BindExpr bindExpr = (BindExpr) e;
                // Set type of binding to type of its target.
                this.inferTypes(bindExpr.getTarget(), suggested);
                bindExpr.setType(bindExpr.getTarget().inferredType());
                // Update type in bindings map.
                this.bindings.put(bindExpr.getLabel(), bindExpr.getType());
                break;
            }
            case RefOp: {
                RefExpr refExpr = (RefExpr) e;
                // Set type of ref to type of its binding or the suggested type.
                DataType dataType = this.bindings.get(refExpr.getLabel());
                if (dataType == null) {
                    panic(String.format("$%s does not have its type set", refExpr.getLabel()));
                }
                refExpr.setType(mostRestrictiveDataType(dataType, suggested));
                break;
            }
            case AndOp: {
                AndExpr andExpr = (AndExpr) e;
                // Assign most restrictive type to And expression.
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
                // Fall back on suggested type, since only type that doesn't match is known.
                this.inferTypes(notExpr.getInput(), suggested);
                notExpr.setType(suggested);
                break;
            }
            case ListOp: {
                ListExpr t = (ListExpr) e;
                // Assign most restrictive type to list expression.
                DataType type = mostRestrictiveDataType(ListDataType, suggested);
                t.setType(type);
                SliceExpr child1 = (SliceExpr) t.child(0);
                for (int i = 0; i < child1.childCount(); i++) {
                    Expr child = child1.child(i);
                    inferTypes(child, AnyDataType);
                }
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
                // Type already known; nothing to infer.
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
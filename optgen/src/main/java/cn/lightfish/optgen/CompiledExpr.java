package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.DefineExpr;
import cn.lightfish.optgen.ast.DefineSetExpr;
import cn.lightfish.optgen.ast.Expr;
import cn.lightfish.optgen.ast.RuleSetExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompiledExpr {
    DefineSetExpr defines;
    RuleSetExpr rules = new RuleSetExpr();
    List<String> defineTags = new ArrayList<>();
    Map<String, DefineExpr> defineIndex = new HashMap<>();
    Map<String, RuleSetExpr> matchIndex = new HashMap<>();

    public DefineExpr lookupDefine(String name){
        return defineIndex.get(name);
    }
    public RuleSetExpr lookupMatchingRules(String name){
        return matchIndex.get(name);
    }

    public DefineSetExpr lookupMatchingDefines(String name){
        DefineSetExpr defineSetExpr = null;
        DefineExpr define = lookupDefine(name);
        if (define != null){
            defineSetExpr = new DefineSetExpr();
            defineSetExpr.append(define);
            return defineSetExpr;
        }else {
            List<DefineExpr> set = this.defines.getSet();
            for (DefineExpr defineExpr : set) {
                if (defineExpr.getTags().contains(name)) {
                    if (defineSetExpr == null){
                        defineSetExpr = new DefineSetExpr();
                    }
                    defineSetExpr.append(defineExpr);
                }
            }
        }
        return defineSetExpr;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(Compiled\n");
        Expr.writeIndent(stringBuilder,1);

        stringBuilder.append("(Defines\n");

        int count1 = defines.childCount();
        for (int i = 0; i < count1; i++) {
            DefineExpr value = defines.child(i);
            Expr.writeIndent(stringBuilder,2);
            value.format(stringBuilder,2);
            stringBuilder.append('\n');
        }
        Expr.writeIndent(stringBuilder,1);
        stringBuilder.append(")\n");

        Expr.writeIndent(stringBuilder,1);
        stringBuilder.append("(Rules\n");

        int count = rules.childCount();
        for (int i = 0; i < count; i++) {
            Expr rule = rules.child(i);
            Expr.writeIndent(stringBuilder,2);
            rule.format(stringBuilder,2);
            stringBuilder.append("\n");
        }
        Expr.writeIndent(stringBuilder,1);
        stringBuilder.append(")\n");
        stringBuilder.append(")\n");

        return stringBuilder.toString();
    }

}
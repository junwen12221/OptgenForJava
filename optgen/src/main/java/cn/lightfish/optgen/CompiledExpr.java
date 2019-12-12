package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.DefineExpr;
import cn.lightfish.optgen.ast.DefineSetExpr;
import cn.lightfish.optgen.ast.Expr;
import cn.lightfish.optgen.ast.RuleSetExpr;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// CompiledExpr is the result of Optgen scanning, parsing, and semantic
// analysis. It contains the set of definitions and rules that were compiled
// from the Optgen input files.
@Data
public class CompiledExpr{
    DefineSetExpr defines;
    RuleSetExpr rules = new RuleSetExpr();
    List<String> defineTags = new ArrayList<>();
    Map<String, DefineExpr> defineIndex = new HashMap<>();
    Map<String, RuleSetExpr> matchIndex = new HashMap<>();

    // LookupDefine returns the DefineExpr with the given name.
    public DefineExpr lookupDefine(String name) {
        return defineIndex.get(name);
    }

    // LookupMatchingRules returns the set of rules that match the given opname at
    // the top-level, or nil if none do. For example, "InnerJoin" would match this
    // rule:
    //   [CommuteJoin]
    //   (InnerJoin $r:* $s:*) => (InnerJoin $s $r)
    public RuleSetExpr lookupMatchingRules(String name) {
        return matchIndex.get(name);
    }

    // LookupMatchingDefines returns the set of define expressions which either
    // exactly match the given name, or else have a tag that matches the given
    // name. If no matches can be found, then LookupMatchingDefines returns nil.
    public DefineSetExpr lookupMatchingDefines(String name) {
        DefineSetExpr defineSetExpr = null;
        DefineExpr define = lookupDefine(name);
        if (define != null) {
            defineSetExpr = new DefineSetExpr();
            defineSetExpr.append(define);
            return defineSetExpr;
        } else {
            // Name might be a tag name, so find all defines with that tag.
            List<DefineExpr> set = this.defines.getSet();
            for (DefineExpr defineExpr : set) {
                if (defineExpr.getTags().contains(name)) {
                    if (defineSetExpr == null) {
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
        Expr.writeIndent(stringBuilder, 1);

        stringBuilder.append("(Defines\n");

        int count1 = defines.childCount();
        for (int i = 0; i < count1; i++) {
            DefineExpr value = defines.child(i);
            Expr.writeIndent(stringBuilder, 2);
            value.format(stringBuilder, 2);
            stringBuilder.append('\n');
        }
        Expr.writeIndent(stringBuilder, 1);
        stringBuilder.append(")\n");

        Expr.writeIndent(stringBuilder, 1);
        stringBuilder.append("(Rules\n");

        int count = rules.childCount();
        for (int i = 0; i < count; i++) {
            Expr rule = rules.child(i);
            Expr.writeIndent(stringBuilder, 2);
            rule.format(stringBuilder, 2);
            stringBuilder.append("\n");
        }
        Expr.writeIndent(stringBuilder, 1);
        stringBuilder.append(")\n");
        stringBuilder.append(")\n");

        return stringBuilder.toString();
    }

}
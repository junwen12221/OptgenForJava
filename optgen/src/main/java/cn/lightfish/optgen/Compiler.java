package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Compiler {

    final Parser parser;
    final CompiledExpr compiled;
    List<String> errors = new ArrayList<>();

    public Compiler(Parser parser, CompiledExpr compiled) {
        this.parser = parser;
        this.compiled = compiled;
    }

    public Compiler(Parser parser) {
        this(parser, new CompiledExpr());
    }

    public static Compiler createFromText(Map<String, String> list) {
        return new Compiler(Parser.createFromText(list));
    }

    public static Compiler createFromFileName(List<String> list) {
        return new Compiler(Parser.createFromFileName(list));
    }

    public void setFileResolver(Function<String, String> resolver) {
        parser.setFileResolver(resolver);
    }

    public CompiledExpr complie() {
        RootExpr root = parser.parse();
        if (root == null) {
            errors.addAll(parser.errors());
            return null;
        }

        if (!compileDeines(root.getDefines())) {
            return null;
        }
        if (compileRules(root.getRules())) {
            return null;
        }
        return compiled;
    }

    private boolean compileRules(RuleSetExpr rules) {
        Map<StringExpr, Boolean> unique = new HashMap<>();

        int count = rules.childCount();
        for (int i = 0; i < count; i++) {
            RuleExpr rule = (RuleExpr)rules.child(i);

            if (unique.containsKey(rule.getName())){
                addErr(rule.source(),String.format("duplicate rule name '%s'",rule.getName().value()));
            }
            unique.put(rule.getName(),true);

            new RuleCompiler().compile(this,rule);
        }
        RuleSetExpr ruleSetExpr = this.compiled.rules;
        int childCount = ruleSetExpr.childCount();
        for (int i = 0; i < childCount; i++) {
            RuleExpr rule = (RuleExpr)ruleSetExpr.child(i);
            String name = rule.getMatch().singleName();
            RuleSetExpr ruleSetExpr1 = compiled.matchIndex.get(name);
            ruleSetExpr1.append(rule);
        }
        return errors.isEmpty();
    }

    private boolean compileDeines(DefineSetExpr defines) {
        this.compiled.defines = defines;

        Map<TagExpr, Boolean> unique = new HashMap<>();

        int count = defines.childCount();
        for (int i = 0; i < count; i++) {
            DefineExpr define = defines.child(i);
            StringExpr name = define.getName();

            if (compiled.defineIndex.containsKey(name.value())) {
                addErr(define.source(), String.format("duplicate '%s' define statement", name));
            }
            compiled.defineIndex.put(name.value(), define);

            TagsExpr tags = define.getTags();
            int count1 = tags.childCount();
            for (int j = 0; j < count1; j++) {
                TagExpr child = tags.child(j);
                if (!unique.containsKey(child)) {
                    compiled.defineTags.add(child.value());
                    unique.put(child, true);
                }
            }
        }
        return true;
    }

    public void addErr(SourceLoc source, String err) {
        if (source != null) {
            errors.add(MessageFormat.format("{0}: {1}", source, err));
        } else {
            errors.add(err);
        }
    }
}
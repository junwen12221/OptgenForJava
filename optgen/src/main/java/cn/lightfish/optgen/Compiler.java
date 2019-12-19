// Copyright 2018 The Cockroach Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied. See the License for the specific language governing
// permissions and limitations under the License.
package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


// Compiler compiles Optgen language input files and builds a CompiledExpr
// result from them. Compilation consists of scanning/parsing the files, which
// produces an AST, and is followed by semantic analysis and limited rewrites
// on the AST which the compiler performs.
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

    // NewCompiler constructs a new instance of the Optgen compiler, with the
    // specified list of file paths as its input files. The Compile method
    // must be called in order to compile the input files.
    public static Compiler createFromFileName(List<String> list) {
        return new Compiler(Parser.createFromFileName(list));
    }

    // SetFileResolver overrides the default method of opening input files. The
    // default resolver will use os.Open to open input files from disk. Callers
    // can use this method to open input files in some other way.
    public void setFileResolver(Function<String, String> resolver) {
        parser.setFileResolver(resolver);
    }

    /**
     * Compile parses and compiles the input files and returns the resulting
     * are returned by the Errors function.
     * @return
     */
    public CompiledExpr complie() {
        RootExpr root = parser.parse();
        if (root == null) {
            errors.addAll(parser.errors());
            return null;
        }

        if (!compileDeines(root.getDefines())) {
            return null;
        }
        if (!compileRules(root.getRules())) {
            return null;
        }
        return compiled;
    }

    private boolean compileRules(RuleSetExpr rules) {
        Map<StringExpr, Boolean> unique = new HashMap<>();

        int count = rules.childCount();
        for (int i = 0; i < count; i++) {
            RuleExpr rule = (RuleExpr) rules.child(i);
            // Ensure that rule names are unique.
            if (unique.containsKey(rule.getName())) {
                addErr(rule.source(), String.format("duplicate rule name '%s'", rule.getName().value()));
            }
            unique.put(rule.getName(), true);

            new RuleCompiler().compile(this, rule);
        }
        RuleSetExpr ruleSetExpr = this.compiled.rules;
        int childCount = ruleSetExpr.childCount();


        // Index compiled rules by the op that they match at the top-level of the
        // rule.
        for (int i = 0; i < childCount; i++) {
            RuleExpr rule = (RuleExpr) ruleSetExpr.child(i);
            String name = rule.getMatch().singleName();
            RuleSetExpr ruleSetExpr1 = compiled.matchIndex.get(name);
            if (ruleSetExpr1 == null) {
                ruleSetExpr1 = new RuleSetExpr();
            }
            ruleSetExpr1.append(rule);
            compiled.matchIndex.put(name, ruleSetExpr1);
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
            // Record the define in the index for fast lookup.
            if (compiled.defineIndex.containsKey(name.value())) {
                addErr(define.source(), String.format("duplicate '%s' define statement", name.value()));
            }
            compiled.defineIndex.put(name.value(), define);

            TagsExpr tags = define.getTags();
            int count1 = tags.childCount();

            // Determine unique set of tags.
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

    public List<String> getErrors() {
        return errors;
    }
}
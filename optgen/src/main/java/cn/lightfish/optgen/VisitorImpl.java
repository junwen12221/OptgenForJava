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
import cn.lightfish.optgen.gen.Matcher;
import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.PatternVisitor;
import cn.lightfish.optgen.gen.node.Order;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

public class VisitorImpl implements PatternVisitor {
    final Map<String, BiFunction> customMap;

    public VisitorImpl(Map<String, BiFunction> customMap) {
        this.customMap = customMap;
    }

    @Override
    public <T> T visit(AndExpr andExpr) {
        return null;
    }

    @Override
    public <T> T visit(AnyExpr anyExpr) {
        return null;
    }

    @Override
    public <T> T visit(BindExpr bindExpr) {
        return null;
    }

    @Override
    public <T> T visit(CommentExpr commentExpr) {
        return null;
    }

    @Override
    public <T> T visit(CommentsExpr expr) {
        return null;
    }

    @Override
    public <T> T visit(CustomFuncExpr customFuncExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineExpr expr) {
        return null;
    }

    @Override
    public <T> T visit(DefineFieldExpr defineFieldExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineFieldsExpr defineFieldsExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineSetExpr defineSetExpr) {
        return null;
    }

    @Override
    public <T> T visit(FuncExpr funcExpr) {
        NameExpr nameExpr = (NameExpr) funcExpr.getName();
        String name = nameExpr.value();

        return null;
    }

    @Override
    public <T> T visit(ListAnyExpr listAnyExpr) {
        return null;
    }

    @Override
    public <T> T visit(ListExpr listExpr) {
        return null;
    }

    @Override
    public <T> T visit(NameExpr nameExpr) {
        return null;
    }

    @Override
    public <T> T visit(NamesExpr namesExpr) {
        return null;
    }

    @Override
    public <T> T visit(NotExpr notExpr) {
        return null;
    }

    @Override
    public <T> T visit(NumberExpr numberExpr) {
        return null;
    }

    @Override
    public <T> T visit(RefExpr refExpr) {
        return null;
    }

    @Override
    public <T> T visit(RootExpr rootExpr) {
        return null;
    }

    @Override
    public <T> T visit(RuleExpr ruleExpr) {
        return null;
    }

    @Override
    public <T> T visit(RuleSetExpr ruleSetExpr) {
        for (RuleExpr ruleExpr : ruleSetExpr.getRuleSetExpr()) {
            StringExpr name = ruleExpr.getName();
            CommentsExpr comments = ruleExpr.getComments();
            TagsExpr tags = ruleExpr.getTags();

            FuncExpr match = ruleExpr.getMatch();

            MatchVisitor matchVisitor = new MatchVisitor();
            System.out.println(match);
            Matcher matcher = match.accept(matchVisitor);

            BoundStatementVisitor boundStatementVisitor = new BoundStatementVisitor(matchVisitor,customMap);


            Expr replace = ruleExpr.getReplace();
            if (replace != null) {
                Factory accept = replace.accept(boundStatementVisitor);
                System.out.println(accept);
                Replacer replacer = new Replacer() {
                    @Override
                    public Node replace(Node o) {
                            Iterator<Node> iterator = o.stream(Order.PREFIX).iterator();
                            while (iterator.hasNext()) {
                                Node next = iterator.next();
                                if (matcher.match(next)) {
                                    Node parent = next.getParent();
                                    if (parent == null&&next == o){
                                        return accept.create(parent);
                                    }
                                    if (parent!=null&&next!=o){
                                        parent.replace(next,accept.create(parent) );
                                    }
                                }
                            }
                            return o;
                    }
                };
                return (T)replacer;
            }
        }

        return null;
    }

    @Override
    public <T> T visit(SliceExpr sliceExpr) {
        return null;
    }

    @Override
    public <T> T visit(StringExpr stringExpr) {
        return null;
    }

    @Override
    public <T> T visit(TagExpr tagExpr) {
        return null;
    }

    @Override
    public <T> T visit(TagsExpr tagsExpr) {
        return null;
    }
}
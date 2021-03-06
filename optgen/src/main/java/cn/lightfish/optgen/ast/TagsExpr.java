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
package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
@EqualsAndHashCode
public class TagsExpr extends Expr{
    List<TagExpr> tagsExpr = new ArrayList<>();
    public TagsExpr() {
        super(Operator.TagsOp);
    }

    @Override
    public int childCount() {
        return tagsExpr.size();
    }

    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TagExpr child(int n) {
        return tagsExpr.get(n);
    }


    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            TagsExpr tagsExpr = new TagsExpr();
            tagsExpr.tagsExpr.addAll((List)exprs);
            return tagsExpr;
        }
        return this;
    }

    public void append(TagExpr tagExpr) {
        tagsExpr.add(tagExpr);
    }

    public boolean contains(String name) {
        for (TagExpr tagExpr : tagsExpr) {
            String i = tagExpr.value();
            if (name.equals(i)) {
                return true;
            }
        }
        return false;
    }
}
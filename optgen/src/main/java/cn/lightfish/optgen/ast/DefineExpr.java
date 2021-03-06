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
import cn.lightfish.optgen.SourceLoc;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static cn.lightfish.optgen.DataType.AnyDataType;

@Getter
@EqualsAndHashCode
public class DefineExpr extends Expr {
    private final SourceLoc sourceLoc;
    CommentsExpr comments;
    final TagsExpr tags;
    StringExpr name;
    DefineFieldsExpr fields = new DefineFieldsExpr();

    public DefineExpr(SourceLoc src, CommentsExpr comments, StringExpr name, TagsExpr tags) {
        super(Operator.DefineOp);
        this.sourceLoc = src;
        this.comments = comments;
        this.name = name;
        this.tags = Objects.requireNonNull(tags);
    }

    @Override
    public int childCount() {
        return 4;
    }

    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        switch (n) {
            case 0:
                return comments;
            case 1:
                return tags;
            case 2:
                return name;
            case 3:
                return fields;
        }
        panic("child index %d is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n) {
            case 0:
                return "Comments";
            case 1:
                return "Tags";
            case 2:
                return "Name";
            case 3:
                return "Fields";
        }
        return "";
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public DataType inferredType() {
        return AnyDataType;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> children = visitChildren(this, visit);
        if (children!=null){
            CommentsExpr comments =(CommentsExpr) children.get(0);
            TagsExpr tagsExpr =(TagsExpr) children.get(1);
            StringExpr name =(StringExpr) children.get(2);
            DefineFieldsExpr fields =(DefineFieldsExpr) children.get(3);

            DefineExpr defineExpr = new DefineExpr(source(), comments, name, tagsExpr);
            defineExpr.append(fields);
            return defineExpr;
        }
        return this;
    }

    public void append(DefineFieldExpr defineField) {
        fields.append(defineField);
    }
    public void append(DefineFieldsExpr defineField) {
        fields.defineFieldsExprs.addAll(defineField.defineFieldsExprs);
    }
}
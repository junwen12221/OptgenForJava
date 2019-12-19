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

@EqualsAndHashCode
public class CommentExpr extends Expr{
    String comment;
    public CommentExpr(String literal) {
        super(Operator.CommentOp);
        this.comment = literal;
    }

    @Override
    public int childCount() {
        return 0;
    }

    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        panic("child index %d is out of range");
        return null;
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public String value() {
        return comment;
    }

    @Override
    public DataType inferredType() {
        return DataType.StringDataType;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this,buff,level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        return this;
    }
}
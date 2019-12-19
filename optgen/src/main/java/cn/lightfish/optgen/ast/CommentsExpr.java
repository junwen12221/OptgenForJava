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
public class CommentsExpr extends Expr{
    List<CommentExpr> commentExprs = new ArrayList<>();
    public CommentsExpr() {
        super(Operator.CommentsOp);
    }

    @Override
    public int childCount() {
        return commentExprs.size();
    }

    @Override
    public   <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public CommentExpr child(int n) {
        return commentExprs.get(n);
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this,buff,level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs != null) {
            CommentsExpr commentsExpr = new CommentsExpr();
            commentsExpr.commentExprs.addAll( (List)exprs);
            return commentsExpr;
        }
        return this;
    }

    public void append(CommentExpr commentExpr) {
        commentExprs.add(commentExpr);
    }
}
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
@Getter
@EqualsAndHashCode
public class DefineFieldExpr extends Expr {
    CommentsExpr commentExpr;
    StringExpr name;
    StringExpr type;
    SourceLoc sourceLoc;



    public DefineFieldExpr(SourceLoc src, StringExpr name, CommentsExpr comments, StringExpr type) {
        super(Operator.DefineFieldOp);
        sourceLoc = src;
        this.name = name;
        this.commentExpr = comments;
        this.type = type;
    }

    @Override
    public int childCount() {
        return 3;
    }

    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return commentExpr;
            case 1:return name;
            case 2:return type;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Comments";
            case 1:return "Name";
            case 2:return "Type";
        }
        return "";
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }
    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            return new DefineFieldExpr(source(),name,commentExpr,type);
        }
        return this;

    }
}
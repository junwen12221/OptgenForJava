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
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class BindExpr extends Expr {
    StringExpr label;
    Expr target;
    SourceLoc sourceLoc;

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    DataType type;


    public BindExpr(SourceLoc src, StringExpr label, Expr target) {
        super(Operator.BindOp);
        this.sourceLoc = src;
        this.label = label;
        this.target = target;
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public   <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return label;
            case 1:return target;
        }
        panic("child index {0} is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Label";
            case 1:return "Target";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this
                , visit);
        if (exprs != null) {
            return new BindExpr(source(),(StringExpr) exprs.get(0), exprs.get(1));
        }
        return this;
    }
}
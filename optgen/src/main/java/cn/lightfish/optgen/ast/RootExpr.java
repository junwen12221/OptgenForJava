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
public class RootExpr extends Expr {
    DefineSetExpr defines = new DefineSetExpr();
    RuleSetExpr rules = new RuleSetExpr();
    SourceLoc src;

    public RootExpr() {
        super(Operator.RootOp);
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public<T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        switch (n) {
            case 0:
                return defines;
            case 1:
                return rules;
        }
        panic("child index %d is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n) {
            case 0:
                return "Defines";
            case 1:
                return "Rules";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> children = visitChildren(this, visit);
        if (children != null) {
            RootExpr rootExpr = new RootExpr();
            rootExpr.defines = (DefineSetExpr) children.get(0);
            rootExpr.rules = (RuleSetExpr) children.get(1);
            rootExpr.src = this.src;
            return rootExpr;
        }
        return this;
    }

    public void appendRule(RuleExpr rule) {
        rules.append(rule);
    }

    public void append(DefineExpr define) {
        defines.append(define);
    }

    @Override
    public SourceLoc source() {
        return src;
    }
}
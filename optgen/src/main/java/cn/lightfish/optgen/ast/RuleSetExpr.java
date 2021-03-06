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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
public class RuleSetExpr extends Expr {
    List<RuleExpr> ruleSetExpr = new ArrayList<>();

    public RuleSetExpr() {
        super(Operator.RuleSetOp);
    }

    @Override
    public int childCount() {
        return ruleSetExpr.size();
    }

    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        return ruleSetExpr.get(n);
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> children = visitChildren(this, visit);
        if (children != null) {
            RuleSetExpr defineSetExpr = new RuleSetExpr();
            defineSetExpr.ruleSetExpr.addAll((List) children);
            return defineSetExpr;
        }
        return this;
    }

    public void append(RuleExpr rule) {
        ruleSetExpr.add(rule);
    }
}
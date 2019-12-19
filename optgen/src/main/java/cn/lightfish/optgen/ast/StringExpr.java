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
public class StringExpr extends Expr{


    private String literal;

    public StringExpr(String literal) {
        super(Operator.StringOp);
        this.literal = literal;
    }

    @Override
    public String value() {
        return literal;
    }

    @Override
    public  <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public DataType inferredType() {
        return DataType.StringDataType;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        return this;
    }

}
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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class RefExpr extends Expr {
    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    private final SourceLoc sourceLoc;
    StringExpr label;
    DataType type;

    public RefExpr(SourceLoc sourceLoc, StringExpr label) {
        super(Operator.RefOp);
        this.sourceLoc = sourceLoc;
        this.label = label;
    }

    @Override
    public int childCount() {
        return 1;
    }

    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        if (n == 0) {
            return label;
        }
        return null;
    }

    @Override
    public String childName(int n) {
        if (n == 0) {
            return "Label";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            Expr expr = exprs.get(0);
            return new RefExpr(source(),(StringExpr)expr);
        }
        return this;
    }
}


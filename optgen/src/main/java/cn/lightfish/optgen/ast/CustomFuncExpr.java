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
public class CustomFuncExpr extends Expr {
    private final SourceLoc sourceLoc;
    Expr name;
    SliceExpr args;
    DataType type;


    public CustomFuncExpr(Expr funcName, SliceExpr args, SourceLoc source) {
        super(Operator.CustomFuncOp);
        this.name = funcName;
        this.args = args;
        sourceLoc = source;
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public  <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return name;
            case 1:return args;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Name";
            case 1:return "Args";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs != null) {
            return new CustomFuncExpr(exprs.get(0),(SliceExpr) exprs.get(1),source());
        }
        return this;
    }
}
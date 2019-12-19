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
import lombok.Setter;

import java.util.List;

@Setter
@EqualsAndHashCode
public class ListExpr extends Expr {
    private final SourceLoc sourceLoc;
    DataType type;
    SliceExpr items = new SliceExpr();
    public ListExpr( SourceLoc sourceLoc) {
        super(Operator.ListOp);
        this.sourceLoc = sourceLoc;
    }

    @Override
    public int childCount() {
        return 1;
    }

    @Override
    public<T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
       if (n == 0){
           return items;
       }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        if (n == 0) {
            return "Items";
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
            ListExpr listExpr = new ListExpr(source());
            listExpr.items =(SliceExpr) exprs.get(0);
            return listExpr;
        }
        return this;
    }

    public void append(Expr item) {
        items.append(item);
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }
}
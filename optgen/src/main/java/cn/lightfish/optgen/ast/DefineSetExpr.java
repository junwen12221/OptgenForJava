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

import static cn.lightfish.optgen.DataType.AnyDataType;

@Data
@EqualsAndHashCode
public class DefineSetExpr extends Expr {
    final List<DefineExpr> set = new ArrayList<>();

    public DefineSetExpr() {
        super(Operator.DefineSetOp);
    }


    @Override
    public int childCount() {
        return set.size();
    }

    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public DefineExpr child(int n) {
        return set.get(n);
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return AnyDataType;
    }

    public void append(DefineExpr define) {
        set.add(define);
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> children = visitChildren(this, visit);
        if (children != null) {
            DefineSetExpr defineSetExpr = new DefineSetExpr();
            int size = children.size();
            for (int i = 0; i < size; i++) {
                defineSetExpr.append((DefineExpr) children.get(i));
            }
        }
        return this;
    }

    public void append(DefineSetExpr lookupMatchingDefines) {
        set.addAll(lookupMatchingDefines.getSet());
    }
//
//    public DefineSetExpr withTag(String tag) {
//        int count = this.childCount();
//        for (int i = 0; i < count; i++) {
//            DefineExpr defineExpr = this.child(i);
//            defineExpr.getTags().append(new TagExpr(tag));
//        }
//        return double ;
//    }
}
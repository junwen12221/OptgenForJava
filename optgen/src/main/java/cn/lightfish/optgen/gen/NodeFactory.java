// Copyright 2019 lightfish.
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
package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.ast.*;
import cn.lightfish.optgen.gen.node.FunNode;

public class NodeFactory {
    public static FunNode create(DefineExpr expr, FunNode parent) {
        StringExpr name = expr.getName();
        FunNode node = new FunNode(parent, name.value(), expr.inferredType().toString());
        DefineFieldsExpr fields = expr.getFields();
        if (fields != null) {
            int count = fields.childCount();
            for (int i = 0; i < count; i++) {
                node.add(create(node,(DefineFieldExpr) fields.child(i)));
            }
        }
        return node;
    }
    public FunNode createMatch(Expr expr) {
        if (expr instanceof FuncExpr){

        }else if (expr instanceof CustomFuncExpr){

        }
        return null;
    }
    private static FunNode create(FunNode node, DefineFieldExpr child) {
        StringExpr name = child.getName();
        StringExpr type = child.getType();
        return null;
    }
}
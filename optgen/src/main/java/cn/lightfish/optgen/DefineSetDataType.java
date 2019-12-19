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
package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.DefineExpr;
import cn.lightfish.optgen.ast.DefineSetExpr;

public class DefineSetDataType implements DataType {
    final DefineSetExpr defines;

    public DefineSetDataType(DefineSetExpr deines) {
        this.defines = deines;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (defines.childCount()>1){
            stringBuilder.append('[');
        }
        int count = defines.childCount();
        for (int i = 0; i < count; i++) {
            DefineExpr define = defines.child(i);
            if (i!=0){
                stringBuilder.append(" | ");
            }
            stringBuilder.append(define.getName().value());
        }
        if (defines.childCount()>1){
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }
}
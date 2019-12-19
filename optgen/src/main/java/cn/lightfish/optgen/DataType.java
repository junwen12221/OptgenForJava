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
import cn.lightfish.optgen.ast.StringExpr;

import java.util.HashMap;
import java.util.Map;

public interface DataType {
    public static DataType AnyDataType = new ExternalDataType("<any>");
    public static DataType ListDataType = new ExternalDataType("<list>");
    public static DataType StringDataType = new ExternalDataType("<string>");
    public static DataType Int64DataType = new ExternalDataType("<int64>");

    default boolean isBuiltinType() {
        return this == ListDataType || this == StringDataType || this == Int64DataType;
    }

    static boolean isTypeMoreRestrictive(DataType left, DataType right) {
        if (left instanceof DefineSetDataType) {
            if (right instanceof DefineSetDataType) {
                DefineSetDataType left0 = (DefineSetDataType) left;
                DefineSetDataType right0 = (DefineSetDataType) right;
                return left0.defines.childCount() < right0.defines.childCount();
            }
            return true;
        }
        if (left instanceof ExternalDataType) {
            if (left == AnyDataType) {
                return false;
            }
            if (left == ListDataType) {
                return right == AnyDataType;
            }
            return right == ListDataType || right == AnyDataType;
        }
        panic("unhandled data type");
        return false;
    }

    static boolean doTypesContradict(DataType dt1, DataType dt2) {
        if (dt1 == dt2 || dt1 == AnyDataType || dt2 == AnyDataType) {
            return false;
        }
        if (dt1 instanceof DefineSetDataType) {
            if (dt2 instanceof DefineSetDataType) {

                if (((DefineSetDataType) dt2).defines.childCount() > ((DefineSetDataType) dt1).defines.childCount()) {
                    DataType tmp = dt2;
                    dt2 = dt1;
                    dt1 = tmp;
                }
                Map<StringExpr, Boolean> map = new HashMap<>();

                for (DefineExpr defineExpr : ((DefineSetDataType) dt1).defines.getSet()) {
                    map.put(defineExpr.getName(), Boolean.TRUE);
                }
                for (DefineExpr defineExpr : ((DefineSetDataType) dt2).defines.getSet()) {
                    StringExpr name = defineExpr.getName();
                    Boolean aBoolean = map.get(name);
                    if (!Boolean.TRUE.equals(aBoolean)) {
                        return true;
                    }
            }
            return false;
        } else if (dt2 instanceof ExternalDataType) {
            return dt2.isBuiltinType();
        }
    }else if(dt1 instanceof ExternalDataType)

    {
        if (dt2 instanceof DefineSetDataType) {
            return dt1.isBuiltinType();
        } else if (dt2 instanceof ExternalDataType) {
            if (!dt1.isBuiltinType() && !dt2.isBuiltinType()) {
                return ((ExternalDataType) dt1).getName().equals(((ExternalDataType) dt2).getName());
            }
            if (dt1.isBuiltinType() && dt2.isBuiltinType()) {
                return !((ExternalDataType) dt1).getName().equals(((ExternalDataType) dt2).getName());
            }
            return false;
        }
    }

    panic("unhandled data type");
        return false;
}

    static void panic(String unhandled_data_type) {
        throw new IllegalArgumentException(unhandled_data_type);
    }
}
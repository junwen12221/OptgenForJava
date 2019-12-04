package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.DefineExpr;
import cn.lightfish.optgen.ast.StringExpr;

import java.util.HashMap;
import java.util.Map;

public interface DataType {
    public static DataType AnyDataType = new ExternalDataType("<any>");
    public static DataType ListDataType = new ExternalDataType("<list>");
    public static DataType StringDataType = new ExternalDataType("<string>");
    public static DataType Int64DataType = new ExternalDataType("<int54>");

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
            if (left == AnyDataType){
                return false;
            }
            if (left == ListDataType){
                return right == AnyDataType;
            }
            return right==ListDataType||right == AnyDataType;
        }
        panic("unhandled data type");
        return false;
    }
    static boolean doTypesContradict(DataType dt1,DataType dt2){
        if (dt1==dt2||dt1==AnyDataType||dt2==AnyDataType){
            return false;
        }
        if (dt1 instanceof DefineSetDataType){
            if (dt2 instanceof DefineSetDataType){

                if(((DefineSetDataType) dt2).defines.childCount()>((DefineSetDataType) dt1).defines.childCount()){
                    DataType tmp = dt2;
                    dt2 = dt1;
                    dt1 = tmp;
                }
                Map<StringExpr,Boolean> map =  new HashMap<>();

                for (DefineExpr defineExpr : ((DefineSetDataType) dt1).defines.getSet()) {
                    map.put(defineExpr.getName(),true);
                }
                for (DefineExpr defineExpr : ((DefineSetDataType) dt2).defines.getSet()) {
                    if(!map.get(defineExpr.getName())){
                        return true;
                    }
                }
                return false;
            }else if (dt2 instanceof ExternalDataType){
                return dt2.isBuiltinType();
            }
        }else if (dt1 instanceof ExternalDataType){
            if (dt2 instanceof DefineSetDataType){
                return dt1.isBuiltinType();
            }else if (dt2 instanceof ExternalDataType){
                if (!dt1.isBuiltinType()&&!dt2.isBuiltinType()){
                    return ((ExternalDataType) dt1).getName().equals(((ExternalDataType) dt2).getName());
                }
                if (dt1.isBuiltinType()&&dt2.isBuiltinType()){
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
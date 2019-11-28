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
            stringBuilder.append(define.getName());
        }
        if (defines.childCount()>1){
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }
}
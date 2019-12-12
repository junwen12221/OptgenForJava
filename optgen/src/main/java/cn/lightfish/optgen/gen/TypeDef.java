package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.ast.Expr;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TypeDef {
    Class clazz;
    String name;
    String fullName;
    String friendlyName;

    boolean isExpr;
    boolean isPointer;
    boolean passByVal;
    boolean isGenerated;

    List<TypeDef> listItemType;

    public boolean isListType() {
        return listItemType != null && !listItemType.isEmpty();
    }

    public String asParam() {
        if (passByVal){
            return name;
        }
        return String.format("*%s",name);
    }

}
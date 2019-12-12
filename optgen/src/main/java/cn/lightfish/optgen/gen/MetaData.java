package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.CompiledExpr;
import cn.lightfish.optgen.ast.DefineExpr;
import cn.lightfish.optgen.ast.DefineFieldExpr;
import cn.lightfish.optgen.ast.DefineFieldsExpr;
import cn.lightfish.optgen.ast.Expr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaData {
    CompiledExpr compiled;
    Map<Class, TypeDef> exprTypes;
    Map<String, TypeDef> types;

    public MetaData(CompiledExpr compiled, List<TypeDef> exprTypes) {
        this.compiled = compiled;

        this.exprTypes = new HashMap<>();
        this.types = new HashMap<>();
        for (TypeDef exprType : exprTypes) {
            this.exprTypes.put(exprType.getClazz(), exprType);
            this.types.put(exprType.getName(),exprType);
        }




    }

    public MetaData(CompiledExpr compiled, Map<Class, TypeDef> exprTypes, Map<String, TypeDef> types) {
        this.compiled = compiled;
        this.exprTypes = exprTypes;
        this.types = types;
    }

    public TypeDef typeOf(Expr e) {
        return exprTypes.get(e);
    }

    public TypeDef lookupType(String friendlyName) {
        return types.get(friendlyName);
    }

    public String fieldName(DefineFieldExpr field) {
        if ("_".equals(field.getName())) {
            return field.getType().value();
        }
        return field.getName().value();
    }

}
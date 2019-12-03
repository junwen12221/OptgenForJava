package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.NameExpr;
import lombok.Data;

@Data
public class ExternalDataType implements DataType{
    NameExpr name;

    public ExternalDataType(NameExpr name) {
        this.name = name;
    }

    public ExternalDataType(String s) {
        this(new NameExpr(s));
    }

    @Override
    public String toString() {
        return this.name.value();
    }
}
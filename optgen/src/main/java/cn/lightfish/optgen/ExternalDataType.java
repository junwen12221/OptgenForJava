package cn.lightfish.optgen;

import lombok.Data;

@Data
public class ExternalDataType implements DataType{
    String name;

    public ExternalDataType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
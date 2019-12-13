package cn.lightfish.optgen.gen.node;

import cn.lightfish.optgen.gen.Node;

import java.util.Objects;

public class NumberNode extends Node {
    Long value;
    public NumberNode(Node parent,Long value) {
        super(parent);
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "int64";
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }

    @Override
    public void replace(Node next, Object o) {

    }
}
package cn.lightfish.optgen.gen.node;

import cn.lightfish.optgen.gen.Node;

public class StringNode extends Node {
    final String value;

    public StringNode(Node parent,String value) {
        super(parent);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "";
    }
}
package cn.lightfish.optgen;

import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.node.FunNode;

@FunctionalInterface
public interface Factory {
    <T> T create(Node parent);
}
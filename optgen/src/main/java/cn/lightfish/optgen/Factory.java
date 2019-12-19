package cn.lightfish.optgen;

import cn.lightfish.optgen.gen.Node;

@FunctionalInterface
public interface Factory {
    <T> T create(Node parent);
}
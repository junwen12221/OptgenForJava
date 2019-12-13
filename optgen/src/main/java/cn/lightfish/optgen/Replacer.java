package cn.lightfish.optgen;

import cn.lightfish.optgen.gen.Node;

@FunctionalInterface
public interface Replacer {
    Node replace(Node o);
}

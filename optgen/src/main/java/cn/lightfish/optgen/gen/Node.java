package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.gen.node.FunNode;
import cn.lightfish.optgen.gen.node.Order;

import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Node {
    public void setParent(Node parent) {
        this.parent = parent;
    }

    Node parent;

    public Node(Node parent) {
        this.parent = parent;
    }

    public String getType(){
        return "Any";
    }
    public Stream<Node> stream(Order order){
        return stream((i)->true,order);
    }

    public Stream<Node> stream(Predicate<Node> predicate, Order order){
        return predicate.test(this)?Stream.of(this):Stream.empty();
    }
}
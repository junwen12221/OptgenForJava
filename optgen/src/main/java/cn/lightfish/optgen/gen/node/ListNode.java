package cn.lightfish.optgen.gen.node;

import cn.lightfish.optgen.gen.Node;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListNode extends Node {
    final List<Node> list;

    public ListNode(Node parent, List<Node> list) {
        super(parent);
        this.list = list;
    }

    /**
     * Getter for property 'list'.
     *
     * @return Value for property 'list'.
     */
    public List<Node> getList() {
        return list;
    }

    @Override
    public Stream<Node> stream(Predicate<Node> predicate, Order order) {
        //valueMap is children
        Stream<Node> childrenStream = this.list.stream().filter(predicate).map(i -> i.stream(predicate, order)).reduce(Stream::concat).orElse(Stream.empty());
        //this is parent
        if (predicate.test(this)) {
            switch (order) {
                case PREFIX:
                    return Stream.concat(Collections.singleton(this).stream(), childrenStream);
                case SUFFIX:
                    return Stream.concat(childrenStream, Collections.singleton(this).stream());
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
            return childrenStream;
        }
    }

    @Override
    public String toString() {
        return this.list.stream().map(i->i.toString()).collect(Collectors.joining(",","[","]"));
    }

    @Override
    public void replace(Node next, Object o) {
      list.set( list.indexOf(next),(Node) o);
    }
}
package cn.lightfish.optgen.gen;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Node {
    final Node parent;
    String name;
    String type;
    final List<Node> valueIndex = new ArrayList<>();

    final Map<String, String> typeMap = new HashMap<>();

    public Node(Node parent, String name, String type) {
        this.parent = parent;
        this.name = name;
        this.type = type;
    }

    public int childNum() {
        return valueIndex.size();
    }

    public String getName() {
        return name;
    }


    public String getChildType(String name) {
        return typeMap.get(name);
    }

    public String getType() {
        return type;
    }


    public enum Order {
        PREFIX, SUFFIX
    }

    public Stream<Node> stream(Order order) {
        return stream((i) -> true, order);
    }

    public Stream<Node> stream(Predicate<Node> predicate, Order order) {
        //valueMap is children
        Stream<Node> childrenStream = this.valueIndex.stream().filter(predicate).map(i -> i.stream(predicate, order)).reduce(Stream::concat).orElse(Stream.empty());
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

    public static void main(String[] args) {
//        Node node = new Node(parent, "1", "");
//        node.add(new Node(parent, "2", ""));
//        node.add(new Node(parent, "3", ""));
//
//        node.stream(Order.PREFIX).forEach(i -> {
//            System.out.println(i.getName());
//        });
    }


    public void add(Node node) {
        typeMap.put(node.name, node.type);
        valueIndex.add(node);
    }

    public List<Node> getValueIndex() {
        return valueIndex;
    }
}
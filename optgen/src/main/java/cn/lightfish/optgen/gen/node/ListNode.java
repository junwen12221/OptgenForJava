// Copyright 2019 lightfish.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied. See the License for the specific language governing
// permissions and limitations under the License.
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
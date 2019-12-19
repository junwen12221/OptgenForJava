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
package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.gen.node.Order;

import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Node{
    Node parent;
    public void setParent(Node parent) {
        this.parent = parent;
    }

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

    public Node getParent() {
        return parent;
    }

    public abstract void replace(Node next, Object o);
}
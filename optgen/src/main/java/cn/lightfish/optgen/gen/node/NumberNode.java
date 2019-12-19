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
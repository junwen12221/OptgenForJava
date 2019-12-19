// Copyright 2018 The Cockroach Authors.
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
package cn.lightfish.optgen;

public enum Operator {
    UnknownOp(0),
    RootOp(1),
    DefineSetOp(2),
    RuleSetOp(3),
    CommentsOp(4),
    CommentOp(5),
    TagsOp(6),
    TagOp(7),
    DefineFieldsOp(8),
    DefineFieldOp(9),
    RuleOp(10),
    FuncOp(11),
    NamesOp(12),
    NameOp(13),
    AndOp(14),
    NotOp(15),
    ListOp(16),
    ListAnyOp(17),
    BindOp(18),
    RefOp(19),
    AnyOp(20),
    SliceOp(21),
    StringOp(22),
    NumberOp(23),
    CustomFuncOp(24), DefineOp(26);

    Operator(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    int value;

}
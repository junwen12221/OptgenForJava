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

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TypeDef {
    Class clazz;
    String name;
    String fullName;
    String friendlyName;

    boolean isExpr;
    boolean isPointer;
    boolean passByVal;
    boolean isGenerated;

    List<TypeDef> listItemType;

    public boolean isListType() {
        return listItemType != null && !listItemType.isEmpty();
    }

    public String asParam() {
        if (passByVal){
            return name;
        }
        return String.format("*%s",name);
    }

}
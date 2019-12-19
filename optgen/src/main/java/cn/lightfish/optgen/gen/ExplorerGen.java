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

import cn.lightfish.optgen.CompiledExpr;
import cn.lightfish.optgen.ast.DefineExpr;
import cn.lightfish.optgen.ast.DefineSetExpr;
import cn.lightfish.optgen.ast.RuleSetExpr;

import java.util.List;

public class ExplorerGen {
    MetaData md;
    final   CompiledExpr compiled;
    public ExplorerGen(CompiledExpr compiled) {
        this.compiled = compiled;
    }
    static class Dispatcher{

        public void add(String name, String value) {

        }
    }
    static class RuleFuncs{

        public void add(String name, String value) {

        }
    }
    public Dispatcher  getDispatcher(){
        Dispatcher dispatcher = new Dispatcher();
        DefineSetExpr defines = compiled.getDefines();
        for (DefineExpr define : defines.getSet()) {
            RuleSetExpr ruleSetExpr = compiled.lookupMatchingRules(define.getName().value());
            if (ruleSetExpr.childCount()>0){
                TypeDef opTyp = md.typeOf(define);
                dispatcher.add(opTyp.getName(),define.getName().value());
            }
        }
        return dispatcher;
    }

    public RuleFuncs  getRuleRuncs(){
        RuleFuncs ruleFuncs = new RuleFuncs();
        DefineSetExpr defines = compiled.getDefines();
        for (DefineExpr define : defines.getSet()) {
            RuleSetExpr ruleSetExpr = compiled.lookupMatchingRules(define.getName().value());
            if (ruleSetExpr == null||ruleSetExpr.childCount()==0){
                continue;
            }
            TypeDef opType = md.typeOf(define);

          //  ruleFuncs.add(opType.getName(),ruleSetExpr,);
        }
        return null;
    }

    List<RuleSetExpr > sortRulesByPriority(RuleSetExpr rules){
        return null;
    }



}
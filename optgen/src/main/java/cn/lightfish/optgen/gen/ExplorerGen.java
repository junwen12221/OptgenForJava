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
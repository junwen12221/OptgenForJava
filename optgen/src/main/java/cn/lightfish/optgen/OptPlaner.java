package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.RuleSetExpr;
import cn.lightfish.optgen.gen.Node;

import java.util.Map;
import java.util.function.Function;

public class OptPlaner {
    private final RuleSetExpr rules;
    private final Estimater estimater;

    public OptPlaner(Map<String, String> map, Map<String, Function<double[], Double>> estimater) {
        Compiler c = Compiler.createFromText(map);
        c.setFileResolver(s -> map.get(s));
        CompiledExpr complied = c.complie();
        if (!c.getErrors().isEmpty()){
            c.getErrors().forEach(i->System.out.println(i));
        }
        this.rules = complied.getRules();
        this.estimater = new Estimater(estimater);



    }

    public Replacer getReplacer() {
        VisitorImpl visitor = new VisitorImpl();
        return rules.accept(visitor);
    }

    public Node opt(Node node) {
        Replacer replacer = getReplacer();
        double old;
        double n;
        Node replace;
        do {
            old = estimater.estimate(node);
            replace = replacer.replace(node);
            n = estimater.estimate(replace);
        } while (old - n >1);
        return replace;
    }
}
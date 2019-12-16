package cn.lightfish.optgen;

import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.node.FunNode;
import cn.lightfish.optgen.gen.node.ListNode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Estimater {
    public Estimater(Map<String, Function<double[], Double>> map) {
        this.map = map;
    }

    final Map<String, Function<double[],Double>> map;

    public double estimate(Node funNode) {
        if (funNode instanceof FunNode){
            FunNode node = (FunNode) funNode;
            int childNum = node.childNum();
            List<Node> valueIndex = node.getValueIndex();
            return innerList(node.getName(), valueIndex);
        }else if (funNode instanceof ListNode){
            ListNode node = (ListNode) funNode;
            return innerList("List",node.getList());
        }else {
            return 1;
        }
    }

    private double innerList(String name, List<Node> valueIndex) {
        double[] es = new double[valueIndex.size()];
        for (int i = 0; i < es.length; i++) {
            es[i]= estimate(valueIndex.get(i));
        }
        Function<double[],Double> doubleSupplier = map.get(name);
        if (doubleSupplier == null){
            return 1;
        }
        return doubleSupplier.apply(es);
    }
}
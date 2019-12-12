package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.ast.*;

public class NodeFactory {
    public static Node create(DefineExpr expr,Node parent) {
        StringExpr name = expr.getName();
        Node node = new Node(parent, name.value(), expr.inferredType().toString());
        DefineFieldsExpr fields = expr.getFields();
        if (fields != null) {
            int count = fields.childCount();
            for (int i = 0; i < count; i++) {
                node.add(create(node,(DefineFieldExpr) fields.child(i)));
            }
        }
        return node;
    }
    public Node createMatch(Expr expr) {
        if (expr instanceof FuncExpr){

        }else if (expr instanceof CustomFuncExpr){

        }
        return null;
    }
    private static Node  create(Node node,DefineFieldExpr child) {
        StringExpr name = child.getName();
        StringExpr type = child.getType();
        return null;
    }
}
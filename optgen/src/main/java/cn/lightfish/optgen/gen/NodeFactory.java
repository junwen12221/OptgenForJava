package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.ast.*;
import cn.lightfish.optgen.gen.node.FunNode;

public class NodeFactory {
    public static FunNode create(DefineExpr expr, FunNode parent) {
        StringExpr name = expr.getName();
        FunNode node = new FunNode(parent, name.value(), expr.inferredType().toString());
        DefineFieldsExpr fields = expr.getFields();
        if (fields != null) {
            int count = fields.childCount();
            for (int i = 0; i < count; i++) {
                node.add(create(node,(DefineFieldExpr) fields.child(i)));
            }
        }
        return node;
    }
    public FunNode createMatch(Expr expr) {
        if (expr instanceof FuncExpr){

        }else if (expr instanceof CustomFuncExpr){

        }
        return null;
    }
    private static FunNode create(FunNode node, DefineFieldExpr child) {
        StringExpr name = child.getName();
        StringExpr type = child.getType();
        return null;
    }
}
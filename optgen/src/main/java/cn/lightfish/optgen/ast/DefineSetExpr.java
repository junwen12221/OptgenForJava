package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;

import java.util.ArrayList;
import java.util.List;

public class DefineSetExpr extends Expr{
   final List<DefineExpr> set = new ArrayList<>();

    public DefineSetExpr() {
        super(Operator.DefineSetOp);
    }


    @Override
    public int childCount() {
        return set.size();
    }

    @Override
    public DefineExpr child(int n) {
        return set.get(n);
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return null;
    }

    public void append(DefineExpr define) {
        set.add(define);
    }
    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }
}
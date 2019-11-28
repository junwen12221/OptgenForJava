package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;

import java.util.ArrayList;
import java.util.List;

public class SliceExpr extends Expr{
   List<Expr> sliceExpr = new ArrayList<>();
    public SliceExpr() {
        super(Operator.SliceOp);
    }


    @Override
    public int childCount() {
        return sliceExpr.size();
    }

    @Override
    public Expr child(int n) {
        return sliceExpr.get(n);
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    public void append(Expr arg) {
        sliceExpr.add(arg);
    }
}
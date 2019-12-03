package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
@EqualsAndHashCode
public class SliceExpr extends Expr {
   private List<Expr> sliceExpr = new ArrayList<>();

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

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs != null) {
            SliceExpr sliceExpr = new SliceExpr();
            sliceExpr.sliceExpr.addAll(exprs);
            return sliceExpr;
        }
        return this;
    }

    public void append(Expr arg) {
        sliceExpr.add(arg);
    }
}
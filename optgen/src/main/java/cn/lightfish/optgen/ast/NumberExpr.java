package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class NumberExpr extends Expr {
    Long number;

    public NumberExpr(long i) {
        super(Operator.NumberOp);
        this.number = i;
    }

    @Override
    public int childCount() {
        return 0;
    }

    @Override
    public Expr  child(int n) {
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return DataType.Int64DataType;
    }

    @Override
    public Expr visit(VisitFunc visit) {
        return this;
    }

    @Override
    public Long value() {
        return number;
    }
}
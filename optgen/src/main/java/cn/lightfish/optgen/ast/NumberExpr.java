package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
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
    public<T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
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
    public Expr visit(ExprVisitFunc visit) {
        return this;
    }

    @Override
    public Long value() {
        return number;
    }
}
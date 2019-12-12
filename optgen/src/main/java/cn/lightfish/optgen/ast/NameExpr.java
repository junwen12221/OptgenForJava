package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class NameExpr extends Expr {
    String nameExpr;
    public NameExpr( String nameExpr) {
        super(Operator.NameOp);
        this.nameExpr = nameExpr;
    }


    @Override
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public DataType inferredType() {
        return DataType.StringDataType;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        return this;
    }

    @Override
    public String value() {
        return nameExpr;
    }
}
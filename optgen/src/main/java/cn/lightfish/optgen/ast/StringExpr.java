package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class StringExpr extends Expr{


    private String literal;

    public StringExpr(String literal) {
        super(Operator.StringOp);
        this.literal = literal;
    }

    @Override
    public String value() {
        return literal;
    }

    @Override
    public DataType inferredType() {
        return DataType.StringDataType;
    }

    @Override
    public Expr visit(VisitFunc visit) {
        return this;
    }

}
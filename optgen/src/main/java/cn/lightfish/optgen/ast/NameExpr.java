package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;

public class NameExpr extends Expr {
    String nameExpr;
    public NameExpr( String nameExpr) {
        super(Operator.NameOp);
        this.nameExpr = nameExpr;
    }


    @Override
    public DataType inferredType() {
        return DataType.StringDataType;
    }

    @Override
    public Expr visit(VisitFunc visit) {
        return this;
    }

    @Override
    public String value() {
        return nameExpr;
    }
}
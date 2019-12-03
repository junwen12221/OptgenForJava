package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class ListAnyExpr extends Expr {
    private final SourceLoc sourceLoc;

    public ListAnyExpr(SourceLoc sourceLoc) {
        super(Operator.ListAnyOp);
        this.sourceLoc = sourceLoc;
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public Expr visit(VisitFunc visit) {
        return this;
    }
}
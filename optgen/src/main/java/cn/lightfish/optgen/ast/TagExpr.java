package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class TagExpr extends Expr {
    private String sourceLoc;

    public TagExpr(String sourceLoc) {
        super(Operator.TagOp);
        this.sourceLoc = sourceLoc;
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
        return sourceLoc;
    }
}

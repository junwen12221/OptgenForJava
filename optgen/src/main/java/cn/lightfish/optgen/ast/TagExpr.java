package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TagExpr extends Expr {
    private String sourceLoc;

    public TagExpr(String sourceLoc) {
        super(Operator.TagOp);
        this.sourceLoc = sourceLoc;
    }

    @Override
    public<T> T accept(PatternVisitor visitor) {
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
        return sourceLoc;
    }
}

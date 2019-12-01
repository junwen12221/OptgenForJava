package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class RefExpr extends Expr {
    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    private final SourceLoc sourceLoc;
    StringExpr label;
    DataType type;

    public RefExpr(SourceLoc sourceLoc, StringExpr label) {
        super(Operator.RefOp);
        this.sourceLoc = sourceLoc;
        this.label = label;
    }

    @Override
    public int childCount() {
        return 1;
    }

    @Override
    public Expr child(int n) {
        if (n == 0) {
            return label;
        }
        return null;
    }

    @Override
    public String childName(int n) {
        if (n == 0) {
            return "Label";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }
}

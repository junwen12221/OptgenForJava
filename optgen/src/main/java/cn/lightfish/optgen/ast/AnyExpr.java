package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class AnyExpr extends Expr {

    private SourceLoc sourceLoc;
    private DataType type;

    public AnyExpr(SourceLoc sourceLoc) {
        super(Operator.AnyOp);
        this.sourceLoc = sourceLoc;
    }

    @Override
    public int childCount() {
        return 0;
    }

    @Override
    public <T extends Expr> T child(int n) {
        panic("child index {0} is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }
}
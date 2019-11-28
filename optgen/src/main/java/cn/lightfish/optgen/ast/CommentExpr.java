package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class CommentExpr extends Expr{
    String comment;
    public CommentExpr(String literal) {
        super(Operator.CommentOp);
        this.comment = literal;
    }

    @Override
    public int childCount() {
        return 0;
    }

    @Override
    public Expr child(int n) {
        panic("child index %d is out of range");
        return null;
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public String value() {
        return comment;
    }

    @Override
    public DataType inferredType() {
        return DataType.StringDataType;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this,buff,level);
    }
}
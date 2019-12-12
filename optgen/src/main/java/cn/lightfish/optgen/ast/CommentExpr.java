package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
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
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
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

    @Override
    public Expr visit(ExprVisitFunc visit) {
        return this;
    }
}
package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
@EqualsAndHashCode
public class CommentsExpr extends Expr{
    List<CommentExpr> commentExprs = new ArrayList<>();
    public CommentsExpr() {
        super(Operator.CommentsOp);
    }

    @Override
    public int childCount() {
        return commentExprs.size();
    }

    @Override
    public   <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public CommentExpr child(int n) {
        return commentExprs.get(n);
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this,buff,level);
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs != null) {
            CommentsExpr commentsExpr = new CommentsExpr();
            commentsExpr.commentExprs.addAll( (List)exprs);
            return commentsExpr;
        }
        return this;
    }

    public void append(CommentExpr commentExpr) {
        commentExprs.add(commentExpr);
    }
}
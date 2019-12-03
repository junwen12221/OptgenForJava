package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
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
    public Expr visit(VisitFunc visit) {
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
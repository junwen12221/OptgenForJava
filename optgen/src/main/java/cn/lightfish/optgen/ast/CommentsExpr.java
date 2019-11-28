package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

import java.util.ArrayList;
import java.util.List;

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

    public void append(CommentExpr commentExpr) {
        commentExprs.add(commentExpr);
    }
}
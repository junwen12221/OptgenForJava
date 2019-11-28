package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

import java.util.ArrayList;
import java.util.List;

public class TagsExpr extends Expr{
    List<TagExpr> tagsExpr = new ArrayList<>();
    public TagsExpr() {
        super(Operator.TagsOp);
    }

    @Override
    public int childCount() {
        return tagsExpr.size();
    }

    @Override
    public TagExpr child(int n) {
        return tagsExpr.get(n);
    }


    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    public void append(TagExpr tagExpr) {
        tagsExpr.add(tagExpr);
    }

}
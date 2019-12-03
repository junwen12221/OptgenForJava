package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
@EqualsAndHashCode
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

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            TagsExpr tagsExpr = new TagsExpr();
            tagsExpr.tagsExpr.addAll((List)exprs);
            return tagsExpr;
        }
        return this;
    }

    public void append(TagExpr tagExpr) {
        tagsExpr.add(tagExpr);
    }

    public boolean contains(String name) {
        for (TagExpr tagExpr : tagsExpr) {
            String i = tagExpr.value();
            if (name.equals(i)) {
                return true;
            }
        }
        return false;
    }
}
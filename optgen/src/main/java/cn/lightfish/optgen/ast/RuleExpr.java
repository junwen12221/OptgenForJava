package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class RuleExpr extends Expr {
    private final SourceLoc sourceLoc;
    private final StringExpr sliceExpr;
    CommentsExpr comments;
    private final TagsExpr tagExprs;
    StringExpr name;
    TagsExpr tags;
    FuncExpr match;
    Expr replace;




    public RuleExpr(SourceLoc src, StringExpr sliceExpr, CommentsExpr comments, TagsExpr tagExprs, FuncExpr match, Expr replace) {
        super(Operator.RuleOp);
        this.sourceLoc = src;
        this.sliceExpr = sliceExpr;
        this.comments = comments;
        this.tagExprs = tagExprs;
        this.match = match;
        this.replace = replace;
    }

    @Override
    public int childCount() {
        return 5;
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return comments;
            case 1:return name;
            case 2:return tags;
            case 3:return match;
            case 4:return replace;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Comments";
            case 1:return "Name";
            case 2:return "Tags";
            case 3:return "Match";
            case 4:return "Replace";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }
}
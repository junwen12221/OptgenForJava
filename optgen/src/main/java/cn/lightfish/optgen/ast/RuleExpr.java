package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.RuleCompiler;
import cn.lightfish.optgen.SourceLoc;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.regex.Matcher;

@Getter
@EqualsAndHashCode
public class RuleExpr extends Expr {
    private final SourceLoc sourceLoc;
    CommentsExpr comments;
    StringExpr name;
    TagsExpr tags;
    FuncExpr match;
    Expr replace;




    public RuleExpr(SourceLoc src, StringExpr name,CommentsExpr comments,  TagsExpr tagExprs, FuncExpr match, Expr replace) {
        super(Operator.RuleOp);
        this.sourceLoc = src;
        this.name = name;
        this.comments = comments;
        this.tags = tagExprs;
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

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            CommentsExpr comments = (CommentsExpr)exprs.get(0);
            StringExpr name = (StringExpr)exprs.get(1);
            TagsExpr tagsExpr = (TagsExpr)exprs.get(2);
            FuncExpr match = (FuncExpr)exprs.get(3);
            Expr replace = exprs.get(4);
            return new RuleExpr(source(),name,comments,tagsExpr,match,replace);
        }
        return this;
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }
}
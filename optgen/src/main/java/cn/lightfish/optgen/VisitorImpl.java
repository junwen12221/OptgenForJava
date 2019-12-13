package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import cn.lightfish.optgen.gen.Matcher;
import cn.lightfish.optgen.gen.PatternVisitor;

public class VisitorImpl implements PatternVisitor {
    @Override
    public <T> T visit(AndExpr andExpr) {
        return null;
    }

    @Override
    public <T> T visit(AnyExpr anyExpr) {
        return null;
    }

    @Override
    public <T> T visit(BindExpr bindExpr) {
        return null;
    }

    @Override
    public <T> T visit(CommentExpr commentExpr) {
        return null;
    }

    @Override
    public <T> T visit(CommentsExpr expr) {
        return null;
    }

    @Override
    public <T> T visit(CustomFuncExpr customFuncExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineExpr expr) {
        return null;
    }

    @Override
    public <T> T visit(DefineFieldExpr defineFieldExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineFieldsExpr defineFieldsExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineSetExpr defineSetExpr) {
        return null;
    }

    @Override
    public <T> T visit(FuncExpr funcExpr) {
        NameExpr nameExpr = (NameExpr) funcExpr.getName();
        String name = nameExpr.value();

        return null;
    }

    @Override
    public <T> T visit(ListAnyExpr listAnyExpr) {
        return null;
    }

    @Override
    public <T> T visit(ListExpr listExpr) {
        return null;
    }

    @Override
    public <T> T visit(NameExpr nameExpr) {
        return null;
    }

    @Override
    public <T> T visit(NamesExpr namesExpr) {
        return null;
    }

    @Override
    public <T> T visit(NotExpr notExpr) {
        return null;
    }

    @Override
    public <T> T visit(NumberExpr numberExpr) {
        return null;
    }

    @Override
    public <T> T visit(RefExpr refExpr) {
        return null;
    }

    @Override
    public <T> T visit(RootExpr rootExpr) {
        return null;
    }

    @Override
    public <T> T visit(RuleExpr ruleExpr) {
        return null;
    }

    @Override
    public <T> T visit(RuleSetExpr ruleSetExpr) {
        for (RuleExpr ruleExpr : ruleSetExpr.getRuleSetExpr()) {
            StringExpr name = ruleExpr.getName();
            CommentsExpr comments = ruleExpr.getComments();
            TagsExpr tags = ruleExpr.getTags();

            FuncExpr match = ruleExpr.getMatch();

            MatchVisitor matchVisitor = new MatchVisitor();
            System.out.println(match);
            Matcher matcher = match.accept(matchVisitor);

            BoundStatementVisitor boundStatementVisitor = new BoundStatementVisitor();


            Expr replace = ruleExpr.getReplace();
            if (replace!=null){
                Factory accept = replace.accept(boundStatementVisitor);
                System.out.println(accept);
            }


            return null;
        }

        return null;
    }

    @Override
    public <T> T visit(SliceExpr sliceExpr) {
        return null;
    }

    @Override
    public <T> T visit(StringExpr stringExpr) {
        return null;
    }

    @Override
    public <T> T visit(TagExpr tagExpr) {
        return null;
    }

    @Override
    public <T> T visit(TagsExpr tagsExpr) {
        return null;
    }
}
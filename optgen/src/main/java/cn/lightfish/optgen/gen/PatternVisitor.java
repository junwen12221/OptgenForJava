package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.ast.*;

public interface PatternVisitor {
    <T> T visit(AndExpr andExpr);

    <T> T visit(AnyExpr anyExpr);

    <T> T visit(BindExpr bindExpr);

    <T> T visit(CommentExpr commentExpr);

    <T> T visit(CommentsExpr expr);

    <T> T visit(CustomFuncExpr customFuncExpr);

    <T> T visit(DefineExpr expr);

    <T> T visit(DefineFieldExpr defineFieldExpr);

    <T> T visit(DefineFieldsExpr defineFieldsExpr);

    <T> T visit(DefineSetExpr defineSetExpr);

    <T> T visit(FuncExpr funcExpr);

    <T> T visit(ListAnyExpr listAnyExpr);

    <T> T visit(ListExpr listExpr);

    <T> T visit(NameExpr nameExpr);

    <T> T visit(NamesExpr namesExpr);

    <T> T visit(NotExpr notExpr);

    <T> T visit(NumberExpr numberExpr);

    <T> T visit(RefExpr refExpr);

    <T> T visit(RootExpr rootExpr);

    <T> T visit(RuleExpr ruleExpr);

    <T> T visit(RuleSetExpr ruleSetExpr);

    <T> T visit(SliceExpr sliceExpr);

    <T> T visit(StringExpr stringExpr);

    <T> T visit(TagExpr tagExpr);

    <T> T visit(TagsExpr tagsExpr);
}
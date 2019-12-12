package cn.lightfish.optgen.ast;

@FunctionalInterface
public interface ExprVisitFunc {
    Expr apply(Expr e);
}
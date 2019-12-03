package cn.lightfish.optgen.ast;

@FunctionalInterface
public interface VisitFunc {
    Expr apply(Expr e);
}
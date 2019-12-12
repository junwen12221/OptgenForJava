package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class NotExpr extends Expr {
    Expr input;
    SourceLoc src;
    DataType type;

    public NotExpr(SourceLoc sourceLoc, Expr input) {
        super(Operator.NotOp);
        this.src = sourceLoc;
        this.input = input;
    }

    @Override
    public int childCount() {
        return 1;
    }

    @Override
    public<T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        if (n == 0) {
            return input;
        }
        panic("child index %d is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        if (n == 0) {
            return "Input";
        }
        return "";
    }

    @Override
    public SourceLoc source() {
        return src;
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            return new NotExpr(source(),exprs.get(0));
        }
        return this;
    }
}
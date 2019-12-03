package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AndExpr extends Expr {
    Expr left;
    Expr right;
    DataType type;
    SourceLoc sourceLoc;

    public AndExpr(SourceLoc src, Expr left, Expr right) {
        super(Operator.AndOp);
        sourceLoc = src;
        this.left = left;
        this.right = right;
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public Expr child(int n) {
        switch (n) {
            case 0:
                return left;
            case 1:
                return right;
        }
        panic("child index {0} is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n) {
            case 0:
                return "Left";
            case 1:
                return "Right";
        }
        return "";
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> exprs = visitChildren(this
                , visit);
        if (exprs != null) {
            return new AndExpr(source(), exprs.get(0), exprs.get(1));
        }
        return this;
    }
}
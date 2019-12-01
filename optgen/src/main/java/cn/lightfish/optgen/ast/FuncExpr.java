package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class FuncExpr extends Expr {
    Expr name;
    SliceExpr args = new SliceExpr();
    DataType type;
    SourceLoc sourceLoc;

    public FuncExpr(SourceLoc sourceLoc, Expr name) {
        super(Operator.FuncOp);
        this.name = name;
        this.sourceLoc = sourceLoc;
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public Expr child(int n) {
        switch (n) {
            case 0:
                return name;
            case 1:
                return args;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n) {
            case 0:
                return "Name";
            case 1:
                return "Args";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    public void append(Expr arg) {
        args.append(arg);
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }
}
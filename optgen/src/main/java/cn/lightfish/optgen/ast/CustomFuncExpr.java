package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class CustomFuncExpr extends Expr {
    private final SourceLoc sourceLoc;
    NameExpr name;
    SliceExpr args;
    DataType type;
    public CustomFuncExpr(SourceLoc sourceLoc) {
        super(Operator.CustomFuncOp);
        this.sourceLoc = sourceLoc;
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return name;
            case 1:return args;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Name";
            case 1:return "Args";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
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
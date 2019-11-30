package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class BindExpr extends Expr {
    StringExpr label;
    Expr target;
    SourceLoc sourceLoc;

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    DataType type;


    public BindExpr(SourceLoc src, StringExpr label, Expr target) {
        super(Operator.BindOp);
        this.sourceLoc = src;
        this.label = label;
        this.target = target;
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return label;
            case 1:return target;
        }
        panic("child index {0} is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Label";
            case 1:return "Target";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }
}
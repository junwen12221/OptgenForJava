package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

public class RootExpr extends Expr {
    DefineSetExpr defines = new DefineSetExpr();
    RuleSetExpr rules = new RuleSetExpr();

    public RootExpr() {
        super(Operator.RootOp);
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return defines;
            case 1:return rules;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Defines";
            case 1:return "Rules";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    public void appendRule(RuleExpr rule) {
        rules.appendRule(rule);
    }

    public void append(DefineExpr define) {
        defines.append(define);
    }
}
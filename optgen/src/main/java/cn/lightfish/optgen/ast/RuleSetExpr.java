package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;

import java.util.ArrayList;
import java.util.List;

public class RuleSetExpr extends Expr {
    List<RuleExpr> ruleSetExpr = new ArrayList<>();

    public RuleSetExpr() {
        super(Operator.RuleSetOp);
    }

    @Override
    public int childCount() {
        return ruleSetExpr.size();
    }

    @Override
    public Expr child(int n) {
        return ruleSetExpr.get(n);
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    public void appendRule(RuleExpr rule) {
        ruleSetExpr.add(rule);
    }
}
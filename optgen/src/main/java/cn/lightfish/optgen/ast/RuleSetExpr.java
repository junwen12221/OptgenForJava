package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
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
    public <T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expr child(int n) {
        return ruleSetExpr.get(n);
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> children = visitChildren(this, visit);
        if (children != null) {
            RuleSetExpr defineSetExpr = new RuleSetExpr();
            defineSetExpr.ruleSetExpr.addAll((List) children);
            return defineSetExpr;
        }
        return this;
    }

    public void append(RuleExpr rule) {
        ruleSetExpr.add(rule);
    }
}
package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

import java.util.ArrayList;
import java.util.List;

public class NamesExpr extends Expr{
  final   List<NameExpr> namesExprs = new ArrayList<>();
    public NamesExpr() {
        super(Operator.NamesOp);
    }

    @Override
    public int childCount() {
        return namesExprs.size();
    }

    @Override
    public NameExpr child(int n) {
        return namesExprs.get(n);
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            NamesExpr namesExpr = new NamesExpr();
            namesExpr.namesExprs.addAll((List)exprs);
            return namesExpr;
        }
        return this;
    }

    public void append(NameExpr nameExpr) {
        namesExprs.add(nameExpr);
    }

}
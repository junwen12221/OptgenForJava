package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
@EqualsAndHashCode
public class DefineFieldsExpr extends Expr {
    List<DefineFieldExpr> defineFieldsExprs = new ArrayList<>();


    public DefineFieldsExpr() {
        super(Operator.DefineFieldsOp);
    }

    @Override
    public int childCount() {
        return defineFieldsExprs.size();
    }

    @Override
    public Expr child(int n) {
        return defineFieldsExprs.get(n);
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
        if (exprs != null) {
            DefineFieldsExpr commentsExpr = new DefineFieldsExpr();
            commentsExpr.defineFieldsExprs.addAll( (List)exprs);
            return commentsExpr;
        }
        return this;
    }

    public void append(DefineFieldExpr defineField) {
        defineFieldsExprs.add(defineField);
    }

}
package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

import java.util.ArrayList;
import java.util.List;

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

    public void append(DefineFieldExpr defineField) {
        defineFieldsExprs.add(defineField);
    }

}
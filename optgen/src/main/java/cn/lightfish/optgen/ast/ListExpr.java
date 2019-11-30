package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;

import java.util.ArrayList;

public class ListExpr extends Expr {
    private final SourceLoc sourceLoc;
    DataType type;
    SliceExpr items = new SliceExpr();
    public ListExpr( SourceLoc sourceLoc) {
        super(Operator.ListOp);
        this.sourceLoc = sourceLoc;
    }

    @Override
    public int childCount() {
        return 1;
    }

    @Override
    public Expr child(int n) {
       if (n == 0){
           return items;
       }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        if (n == 1){{
            return "Item";
        }}
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    public void append(Expr item) {
        items.append(item);
    }
}
package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
@EqualsAndHashCode
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
        if (n == 0) {
            return "Items";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    @Override
    public Expr visit(VisitFunc visit) {
        return this;
    }

    public void append(Expr item) {
        items.append(item);
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }
}
package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import cn.lightfish.optgen.gen.PatternVisitor;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.List;

@Setter
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
    public<T> T accept(PatternVisitor visitor) {
        return visitor.visit(this);
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
    public Expr visit(ExprVisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            ListExpr listExpr = new ListExpr(source());
            listExpr.items =(SliceExpr) exprs.get(0);
            return listExpr;
        }
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
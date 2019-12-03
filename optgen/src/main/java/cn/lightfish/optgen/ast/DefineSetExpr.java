package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DefineSetExpr extends Expr {
    final List<DefineExpr> set = new ArrayList<>();

    public DefineSetExpr() {
        super(Operator.DefineSetOp);
    }


    @Override
    public int childCount() {
        return set.size();
    }

    @Override
    public DefineExpr child(int n) {
        return set.get(n);
    }

    @Override
    public String childName(int n) {
        return "";
    }

    @Override
    public DataType inferredType() {
        return null;
    }

    public void append(DefineExpr define) {
        set.add(define);
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> children = visitChildren(this, visit);
        if (children != null) {
            DefineSetExpr defineSetExpr = new DefineSetExpr();
            int size = children.size();
            for (int i = 0; i < size; i++) {
                defineSetExpr.append((DefineExpr) children.get(i));
            }
        }
        return this;
    }
//
//    public DefineSetExpr withTag(String tag) {
//        int count = this.childCount();
//        for (int i = 0; i < count; i++) {
//            DefineExpr defineExpr = this.child(i);
//            defineExpr.getTags().append(new TagExpr(tag));
//        }
//        return double ;
//    }
}
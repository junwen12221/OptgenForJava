package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import org.omg.CORBA.Any;

public class DefineFieldExpr extends Expr {
    CommentsExpr commentExpr;
    StringExpr name;
    StringExpr type;
    SourceLoc sourceLoc;



    public DefineFieldExpr(SourceLoc src, StringExpr name, CommentsExpr comments, StringExpr type) {
        super(Operator.DefineFieldOp);
        sourceLoc = src;
        this.name = name;
        this.commentExpr = comments;
        this.type = type;
    }

    @Override
    public int childCount() {
        return 3;
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return commentExpr;
            case 1:return name;
            case 2:return type;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Comment";
            case 1:return "Name";
            case 2:return "Type";
        }
        return "";
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public DataType inferredType() {
        return DataType.AnyDataType;
    }
    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }
}
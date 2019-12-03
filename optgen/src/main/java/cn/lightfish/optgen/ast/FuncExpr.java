package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FuncExpr extends Expr {
    Expr name;
    SliceExpr args = new SliceExpr();
    DataType type;
    SourceLoc sourceLoc;

    public FuncExpr(SourceLoc sourceLoc, Expr name) {
        super(Operator.FuncOp);
        this.name = name;
        this.sourceLoc = sourceLoc;
    }

    public FuncExpr(SourceLoc source, Expr funcName, SliceExpr args) {
        this(source, funcName);
        this.args = args;
    }

    @Override
    public int childCount() {
        return 2;
    }

    @Override
    public Expr child(int n) {
        switch (n) {
            case 0:
                return name;
            case 1:
                return args;
        }
        panic("child index %d is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n) {
            case 0:
                return "Name";
            case 1:
                return "Args";
        }
        return "";
    }

    @Override
    public DataType inferredType() {
        return type;
    }

    public void append(Expr arg) {
        args.append(arg);
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> exprs = visitChildren(this, visit);
        if (exprs!=null){
            return new FuncExpr(source(),exprs.get(0),(SliceExpr)exprs.get(1));
        }
        return this;
    }

    public boolean hasDynamicName() {
        return !(this.name instanceof NameExpr) && !(this.name instanceof NamesExpr);
    }


    public String singleName() {
        if (this.name instanceof NamesExpr) {
            List<NameExpr> namesExprs = ((NamesExpr) this.name).namesExprs;
            if (namesExprs.size() > 1) {
                panic("function cannot have more than one name");
            }
            return namesExprs.get(0).value();
        }
        return ((NameExpr) this.name).value();
    }

    public NamesExpr nameChoice() {
        if (this.name instanceof NamesExpr){
            return (NamesExpr)this.name;
        }else {
            NamesExpr namesExpr = new NamesExpr();
            namesExpr.append((NameExpr)this.name);
            return namesExpr;
        }
    }
}
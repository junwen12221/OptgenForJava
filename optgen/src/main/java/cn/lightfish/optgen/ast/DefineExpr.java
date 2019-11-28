package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.Getter;

@Getter
public class DefineExpr extends Expr{
    private final SourceLoc sourceLoc;
    CommentsExpr comments;
    TagsExpr tags;
    StringExpr name;
    DefineFieldsExpr fields = new DefineFieldsExpr();

    public DefineExpr(SourceLoc src, CommentsExpr comments, StringExpr name, TagsExpr tags) {
        super(Operator.DefineOp);
        this.sourceLoc = src;
        this.comments = comments;
        this.name = name;
        this.tags = tags;
    }

    @Override
    public int childCount() {
        return 4;
    }

    @Override
    public Expr child(int n) {
        switch (n){
            case 0:return comments;
            case 1:return tags;
            case 2:return name;
            case 3:return fields;
        }
        panic("child index %d is out of range",n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n){
            case 0:return "Comments";
            case 1:return "Tags";
            case 2:return "Name";
            case 3:return "Fields";
        }
        return "";
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public DataType inferredType() {
        return null;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    public void append(DefineFieldExpr defineField) {
        fields.append(defineField);
    }
}
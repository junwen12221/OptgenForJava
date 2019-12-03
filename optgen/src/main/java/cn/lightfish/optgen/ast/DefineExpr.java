package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static cn.lightfish.optgen.DataType.AnyDataType;

@Getter
public class DefineExpr extends Expr {
    private final SourceLoc sourceLoc;
    CommentsExpr comments;
    final TagsExpr tags;
    StringExpr name;
    DefineFieldsExpr fields = new DefineFieldsExpr();

    public DefineExpr(SourceLoc src, CommentsExpr comments, StringExpr name, TagsExpr tags) {
        super(Operator.DefineOp);
        this.sourceLoc = src;
        this.comments = comments;
        this.name = name;
        this.tags = Objects.requireNonNull(tags);
    }

    @Override
    public int childCount() {
        return 4;
    }

    @Override
    public Expr child(int n) {
        switch (n) {
            case 0:
                return comments;
            case 1:
                return tags;
            case 2:
                return name;
            case 3:
                return fields;
        }
        panic("child index %d is out of range", n);
        return null;
    }

    @Override
    public String childName(int n) {
        switch (n) {
            case 0:
                return "Comments";
            case 1:
                return "Tags";
            case 2:
                return "Name";
            case 3:
                return "Fields";
        }
        return "";
    }

    @Override
    public SourceLoc source() {
        return sourceLoc;
    }

    @Override
    public DataType inferredType() {
        return AnyDataType;
    }

    @Override
    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    @Override
    public Expr visit(VisitFunc visit) {
        List<Expr> children = visitChildren(this, visit);
        if (children!=null){
            CommentsExpr comments =(CommentsExpr) children.get(0);
            TagsExpr tagsExpr =(TagsExpr) children.get(1);
            StringExpr name =(StringExpr) children.get(2);
            DefineFieldsExpr fields =(DefineFieldsExpr) children.get(3);

            DefineExpr defineExpr = new DefineExpr(source(), comments, name, tagsExpr);
            defineExpr.append(fields);
            return defineExpr;
        }
        return this;
    }

    public void append(DefineFieldExpr defineField) {
        fields.append(defineField);
    }
    public void append(DefineFieldsExpr defineField) {
        fields.defineFieldsExprs.addAll(defineField.defineFieldsExprs);
    }
}
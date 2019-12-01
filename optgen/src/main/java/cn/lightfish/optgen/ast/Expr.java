package cn.lightfish.optgen.ast;

import cn.lightfish.optgen.DataType;
import cn.lightfish.optgen.Operator;
import cn.lightfish.optgen.SourceLoc;
import lombok.SneakyThrows;

import java.text.MessageFormat;
import java.util.Objects;

public abstract class Expr {
    Operator op;

    public Expr(Operator op) {
        this.op = op;
    }

    Operator op() {
        return op;
    }

    public  int childCount(){
        return 0;
    }

    public  <T extends Expr> T child(int n){
        panic("child index %d is out of range",n);
        return null;
    }

    public  String childName(int n){
        return "";
    }

    public <T> T value() {
        return null;
    }

    public SourceLoc source() {
        return null;
    }

    public abstract DataType inferredType();

    public void format(Appendable buff, int level) {
        format(this, buff, level);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        format(this, stringBuilder, 0);
        return stringBuilder.toString();
    }


    @SneakyThrows
    protected static void format(Expr e, Appendable buff, int level) {
        try {
            if (e.value() != null) {
                switch (e.op()) {
                    case StringOp: {
                        buff.append('"');
                        buff.append(e.value());
                        buff.append('"');
                        break;
                    }
                    case NumberOp: {
                        buff.append(Objects.toString(e.value()));
                        break;
                    }
                    default: {
                        buff.append(Objects.toString(e.value()));
                        break;
                    }
                }
                return;
            }
            String name0 = e.op().name();
            String opName = name0.substring(0, name0.length() - 2);
            SourceLoc src = e.source();

            boolean nested = false;
            for (int j = 0; j < e.childCount(); j++) {
                Expr child = e.child(j);
                if (child.value() == null && child.childCount() != 0) {
                    nested = true;
                    break;
                }
            }
            if (!nested) {
                buff.append('(');
                buff.append(opName);

                for (int j = 0; j < e.childCount(); j++) {
                    buff.append(' ');
                    opName = e.childName(j);
                    if (!"".equals(opName)) {
                        buff.append(opName);
                        buff.append('=');
                    }
                    e.child(j).format(buff, level);
                }

                DataType type = e.inferredType();
                if (type != null && !type.equals(DataType.AnyDataType)) {
                    buff.append(" Typ=").append(String.valueOf(e.inferredType()));
                }
                if (src != null && e.childCount() != 0) {
                    buff.append(" Src=<").append(src.toString()).append(">");
                }
                buff.append(")");
            } else {
                buff.append("(").append(opName).append('\n');
                level++;

                for (int i = 0; i < e.childCount(); i++) {
                    writeIndent(buff, level);

                    String name = e.childName(i);
                    if (!"".equals(name)) {
                        buff.append(name);
                        buff.append('=');
                    }
                    Expr child = e.child(i);
                    try {
                        child.format(buff, level);
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                    buff.append('\n');
                }
                DataType typ = e.inferredType();
                if (typ != null && typ != DataType.AnyDataType) {
                    writeIndent(buff, level);
                    buff.append("Typ=").append(String.valueOf(e.inferredType())).append('\n');
                }
                if (src != null && e.childCount() != 0) {
                    writeIndent(buff, level);
                    buff.append("Src=<").append(String.valueOf(src)).append(">").append('\n');
                }
                level--;
                writeIndent(buff, level);
                buff.append(')');
            }
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

    @SneakyThrows
    private static void writeIndent(Appendable stringBuilder, int level) {
        for (int i = 0; i < level; i++) {
            stringBuilder.append('\t');
        }
    }


    protected void panic(String format, Object... args) {
        throw new IllegalArgumentException(MessageFormat.format(format, args));
    }
}
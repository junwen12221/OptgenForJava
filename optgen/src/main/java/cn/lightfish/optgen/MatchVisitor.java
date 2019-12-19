package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import cn.lightfish.optgen.gen.Matcher;
import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.PatternVisitor;
import cn.lightfish.optgen.gen.node.FunNode;
import cn.lightfish.optgen.gen.node.ListNode;
import cn.lightfish.optgen.gen.node.NumberNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchVisitor implements PatternVisitor {
    final Map bind;
    final boolean noMatch;

    public MatchVisitor() {
        this(new HashMap<>(), false);
    }

    public MatchVisitor(Map bind, boolean noMatch) {
        this.bind = bind;
        this.noMatch = noMatch;
    }

    @Override
    public Matcher visit(AndExpr andExpr) {
        Matcher left = andExpr.getLeft().accept(this);
        Matcher right = andExpr.getRight().accept(this);
        return arg -> left.match(arg) && right.match(arg);
    }

    @Override
    public Matcher visit(AnyExpr anyExpr) {
        return arg -> true;
    }

    @Override
    public Matcher visit(BindExpr bindExpr) {
        String label = bindExpr.getLabel().value();
        Matcher matcher = bindExpr.getTarget().accept(this);
        return arg -> {
            if (matcher.match(arg)) {
                bind.put(label, arg);
                return true;
            } else {
                return false;
            }

        };
    }

    @Override
    public <T> T visit(CommentExpr commentExpr) {
        return null;
    }

    @Override
    public <T> T visit(CommentsExpr expr) {
        return null;
    }

    @Override
    public Matcher visit(CustomFuncExpr customFuncExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineExpr expr) {
        return null;
    }

    @Override
    public <T> T visit(DefineFieldExpr defineFieldExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineFieldsExpr defineFieldsExpr) {
        return null;
    }

    @Override
    public <T> T visit(DefineSetExpr defineSetExpr) {
        return null;
    }

    @Override
    public Matcher visit(FuncExpr funcExpr) {
        Matcher name = funcExpr.getName().accept(this);
        SliceExpr args = funcExpr.getArgs();
        int count = args.childCount();
        List<Matcher> argMatchers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Expr child = args.child(i);
            argMatchers.add(child.accept(this));
        }
        if (this.noMatch && funcExpr.getArgs().childCount() != 0) {
            throw new UnsupportedOperationException();
        }
        String type = funcExpr.getType().toString();
        return arg -> {
            if (arg instanceof FunNode) {
                FunNode node = (FunNode) arg;
                boolean match = name.match(((FunNode) arg).getName());
                boolean equals = type.equals(node.getType());
                int argNum = node.childNum();
                if (!match||argNum != argMatchers.size()){
                    return false;
                }
                for (int i = 0; i < argNum; i++) {
                    Matcher matcher = argMatchers.get(i);
                    Node node1 = node.getValueIndex().get(i);
                    if(!matcher.match(node1)){
                        return false;
                    }
                }
                return  true;
            } else {
                return false;
            }
        };
    }

    @Override
    public Matcher visit(ListAnyExpr listAnyExpr) {
        return arg -> false;
    }

    @Override
    public Matcher visit(ListExpr listExpr) {
        int count = listExpr.childCount();
        SliceExpr items = (SliceExpr)listExpr.child(0);
        boolean isFirst = false;
        boolean isLast = false;
        Expr matchItem = null;
        for (int i = 0; i < items.childCount(); i++) {
            Expr child = items.child(i);
            if (child.op() != Operator.ListAnyOp) {
                matchItem = child;
                if (i == 0) {
                    isFirst = true;
                }
                if (i == (count - 1)) {
                    isLast = true;
                }
            }
        }

        if (matchItem == null) {
            if (count == 0) {
                if (this.noMatch) {
                    return arg -> {
                        if (arg instanceof List) {
                            return ((List) arg).size() != 0;
                        }
                        return false;
                    };
                } else {
                    return arg -> {
                        if (arg instanceof List) {
                            return ((List) arg).size() == 0;
                        }
                        return false;
                    };
                }
            }
            return arg -> true;
        }
        Matcher next = matchItem.accept(this);
        if (isFirst && isLast) {
            if (noMatch) {
                if (matchItem.op() != Operator.AnyOp) {
                    throw new UnsupportedOperationException();
                }
                return arg -> {
                    if (arg instanceof ListNode) {
                        return ((ListNode) arg).getList().size() != 1;
                    }
                    return false;
                };
            }

            return arg -> {
                if (arg instanceof List) {
                    return ((List) arg).size() == 1 && next.match(((List) arg).get(0));
                }
                return false;
            };
        } else if (isFirst && !isLast) {
            if (noMatch) {
                throw new UnsupportedOperationException();
            }
            return arg -> {
                if (arg instanceof List) {
                    return ((List) arg).size() > 0 && next.match(((List) arg).get(0));
                }
                return false;
            };
        } else if (!isFirst && isLast) {
            if (noMatch) {
                throw new UnsupportedOperationException();
            }
            return arg -> {
                if (arg instanceof List) {
                    int size = ((List) arg).size();
                    return size > 0 && next.match(((List) arg).get(size - 1));
                }
                return false;
            };
        } else if (!isFirst && !isLast) {
            if (noMatch) {
                throw new UnsupportedOperationException();
            }
            ArrayList<Matcher> list = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                list.add(items.child(i).accept(this));
            }
            return arg -> {
                if (arg instanceof List) {
                    List l = (List) arg;
                    for (Object o : l) {
                        if (next.match(o)) {
                            return true;
                        }
                    }
                }
                return false;
            };
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Matcher visit(NameExpr nameExpr) {
        String value = nameExpr.value();
        return arg -> value.equals(arg);
    }

    @Override
    public Matcher visit(NamesExpr namesExpr) {
        return arg -> {
            if (arg instanceof ListNode) {
                ListNode list = (ListNode) arg;
                if (list.getList().size() == namesExpr.childCount()) {
                    for (int i = 0; i < list.getList().size(); i++) {
                        if (!namesExpr.child(i).value().equals(list.getList().get(i))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        };
    }

    @Override
    public Matcher visit(NotExpr notExpr) {
        MatchVisitor matchVisitor = new MatchVisitor(this.bind, false);
        return notExpr.getInput().accept(matchVisitor);
    }

    @Override
    public Matcher visit(NumberExpr numberExpr) {
        Long value = numberExpr.value();
        return obj -> {
            if (obj instanceof NumberNode ){
                return value.equals(((NumberNode) obj).getValue());
            }
            return false;
        };
    }

    @Override
    public Matcher visit(RefExpr refExpr) {
        return null;
    }

    @Override
    public Matcher visit(RootExpr rootExpr) {
        return null;
    }

    @Override
    public Matcher visit(RuleExpr ruleExpr) {
        return null;
    }

    @Override
    public Matcher visit(RuleSetExpr ruleSetExpr) {
        return null;
    }

    @Override
    public Matcher visit(SliceExpr sliceExpr) {
     return null;
    }

    @Override
    public Matcher visit(StringExpr stringExpr) {
        String value = stringExpr.value();
        return arg -> value.equals(arg);
    }

    @Override
    public Matcher visit(TagExpr tagExpr) {
        return null;
    }

    @Override
    public Matcher visit(TagsExpr tagsExpr) {
        return null;
    }

    public Map getBind() {
        return bind;
    }
}
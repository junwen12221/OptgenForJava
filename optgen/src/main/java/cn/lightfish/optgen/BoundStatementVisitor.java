package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.node.FunNode;
import cn.lightfish.optgen.gen.NodeFactory;
import cn.lightfish.optgen.gen.PatternVisitor;
import cn.lightfish.optgen.gen.node.ListNode;
import cn.lightfish.optgen.gen.node.NumberNode;
import cn.lightfish.optgen.gen.node.StringNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BoundStatementVisitor implements PatternVisitor {
    final Map<String,Object> bind ;
    final Map<String, BiFunction> customMap = new HashMap<>();

    public BoundStatementVisitor(MatchVisitor matchVisitor) {

        this.bind = matchVisitor.bind;
    }

    @Override
    public Factory visit(AndExpr andExpr) {
        return null;
    }

    @Override
    public Factory visit(AnyExpr anyExpr) {
        return null;
    }

    @Override
    public Factory visit(BindExpr bindExpr) {
        return null;
    }

    @Override
    public Factory visit(CommentExpr commentExpr) {
        return null;
    }

    @Override
    public Factory visit(CommentsExpr expr) {
        return null;
    }

    @Override
    public Factory visit(CustomFuncExpr customFuncExpr) {
        NameExpr name = (NameExpr) customFuncExpr.getName();
        BiFunction function = customMap.get(name.value());
        SliceExpr args = customFuncExpr.getArgs();
        int count = args.childCount();

        List<Factory> argList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            argList.add(args.child(i).accept(this));
        }
        return new Factory() {
            @Override
            public Node create(Node parent) {
                List<Node> collect = (List)argList.stream().map(i -> create(null)).collect(Collectors.toList());
                Node apply = (Node)function.apply(parent, collect);
                collect.stream().forEach(i->i.setParent(apply));
                return apply;
            }
        };
    }

    @Override
    public Factory visit(DefineExpr expr) {
        return null;
    }

    @Override
    public Factory visit(DefineFieldExpr defineFieldExpr) {
        return null;
    }

    @Override
    public Factory visit(DefineFieldsExpr defineFieldsExpr) {
        return null;
    }

    @Override
    public Factory visit(DefineSetExpr defineSetExpr) {
        return null;
    }

    @Override
    public Factory visit(FuncExpr funcExpr) {
        NameExpr nameExpr = (NameExpr) funcExpr.getName();
        String value = nameExpr.value();
        SliceExpr args = funcExpr.getArgs();
        int count = args.childCount();
        List<Factory> arglist = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            arglist.add(args.child(i).accept(this));
        }
        return new Factory() {
            @Override
            public FunNode create(Node parent) {
                FunNode funNode = new FunNode(parent,value,"");
                for (Factory i : arglist) {
                    Node node = i.create(funNode);
                    funNode.add(node);
                }
                return funNode;
            }
        };
    }

    @Override
    public Factory visit(ListAnyExpr listAnyExpr) {
        return null;
    }

    @Override
    public Factory visit(ListExpr listExpr) {
        ArrayList<Factory> list = new ArrayList<>();
        SliceExpr sliceExpr = (SliceExpr) listExpr.child(0);
        int count = sliceExpr.childCount();
        for (int i = 0; i < count; i++) {
            list.add(sliceExpr.child(i).accept(this));
        }
        return new Factory() {
            @Override
            public Node create(Node parent) {
                return new ListNode(parent, (List) list.stream().map(i->i.create(parent)).collect(Collectors.toList()));
            }
        };
    }

    @Override
    public Factory visit(NameExpr nameExpr) {
        String name = nameExpr.value();
        Node o = (Node) bind.get(name);
        return new Factory() {
            @Override
            public Node create(Node parent) {
                o.setParent(parent);
                return o;
            }
        };
    }

    @Override
    public Factory visit(NamesExpr namesExpr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Factory visit(NotExpr notExpr) {
     throw new UnsupportedOperationException();
    }

    @Override
    public Factory visit(NumberExpr numberExpr) {
        Long value = numberExpr.value();
        return new Factory() {
            @Override
            public NumberNode create(Node parent) {
                return new NumberNode(parent,value);
            }
        };
    }

    @Override
    public Factory visit(RefExpr refExpr) {
        String value = refExpr.getLabel().value();
        return new Factory() {
            @Override
            public <T> T create(Node parent) {
                return (T)bind.get(value);
            }
        };
    }

    @Override
    public Factory visit(RootExpr rootExpr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Factory visit(RuleExpr ruleExpr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Factory visit(RuleSetExpr ruleSetExpr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Factory visit(SliceExpr sliceExpr) {
        throw new UnsupportedOperationException();
//        int count = sliceExpr.childCount();
//        ArrayList<Factory> list = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            list.add(sliceExpr.child(i).accept(this));
//        }
//        return new Factory() {
//            @Override
//            public List create(Node parent) {
//                return list.stream().map(i->i.create()).collect(Collectors.toList());
//            }
//        };
    }

    @Override
    public Factory visit(StringExpr stringExpr) {
        String value = stringExpr.value();
        return new Factory() {
            @Override
            public StringNode create(Node parent){
                return new StringNode(parent,value);
            }
        };
    }

    @Override
    public Factory visit(TagExpr tagExpr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Factory visit(TagsExpr tagsExpr) {
        throw new UnsupportedOperationException();
    }
}
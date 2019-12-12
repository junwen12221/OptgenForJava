package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.NodeFactory;
import cn.lightfish.optgen.gen.PatternVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BoundStatementVisitor implements PatternVisitor {
    final NodeFactory factory = new NodeFactory();
    final Map<String,Object> bind = new HashMap<>();
    final Map<String, Function> customMap = new HashMap<>();

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
        Function function = customMap.get(name.value());
        Factory args = customFuncExpr.getArgs().accept(this);
        return new Factory() {
            @Override
            public <T> T create() {
                Object o1 = args.create();
                return (T)function.apply(o1);
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
        Factory accept = funcExpr.getArgs().accept(this);
        return new Node(,value,"");
    }

    @Override
    public Factory visit(ListAnyExpr listAnyExpr) {
        return null;
    }

    @Override
    public Factory visit(ListExpr listExpr) {
        ArrayList<Factory> list = new ArrayList<>();
        int count = listExpr.childCount();
        for (int i = 0; i < count; i++) {
            list.add(listExpr.child(i).accept(this));
        }
        return new Factory() {
            @Override
            public List create(Node parent) {
                return list.stream().map(i->i.create(parent)).collect(Collectors.toList());
            }
        };
    }

    @Override
    public Factory visit(NameExpr nameExpr) {
        String value = nameExpr.value();
        return new Factory() {
            @Override
            public String create(Node parent){
                return value;
            }
        };
    }

    @Override
    public Factory visit(NamesExpr namesExpr) {
        int count = namesExpr.childCount();
        ArrayList<Factory> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(namesExpr.child(i).accept(this));
        }
        return new Factory() {
            @Override
            public List<String> create(Node parent) {
                List<String> list1 = new ArrayList<>();
                for (Factory factory1 : list) {
                    list1.add(factory1.create());
                }
                return list1;
            }
        };
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
            public Long create(Node parent) {
                return value;
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
        int count = sliceExpr.childCount();
        ArrayList<Factory> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(sliceExpr.child(i).accept(this));
        }
        return new Factory() {
            @Override
            public List create(Node parent) {
                return list.stream().map(i->i.create()).collect(Collectors.toList());
            }
        };
    }

    @Override
    public Factory visit(StringExpr stringExpr) {
        String value = stringExpr.value();
        return new Factory() {
            @Override
            public String create(Node parent){
                return value;
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
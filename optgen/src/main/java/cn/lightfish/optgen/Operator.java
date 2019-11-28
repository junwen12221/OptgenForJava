package cn.lightfish.optgen;

public enum Operator {
    UnknownOp(0),
    RootOp(1),
    DefineSetOp(2),
    RuleSetOp(3),
    CommentsOp(4),
    CommentOp(5),
    TagsOp(6),
    TagOp(7),
    DefineFieldsOp(8),
    DefineFieldOp(9),
    RuleOp(10),
    FuncOp(11),
    NamesOp(12),
    NameOp(13),
    AndOp(14),
    NotOp(15),
    ListOp(16),
    ListAnyOp(17),
    BindOp(18),
    RefOp(19),
    AnyOp(20),
    SliceOp(21),
    StringOp(22),
    NumberOp(23),
    CustomFuncOp(24), DefineOp(26);

    Operator(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    int value;

}
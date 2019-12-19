package cn.lightfish.optgen;

import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.node.FunNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class OptPlanerTest {

    @Test
    public void test(){
        String s1 = "define Join {\n" +
                "    # Left comment.\n" +
                "    Left  Expr\n" +
                "\n" +
                "    # Right comment.\n" +
                "    Right Expr\n" +
                "}\n" +
                "\n" +
                "# CommuteJoin comment.\n" +
                "[CommuteJoin]\n" +
                "(Join $left:* $right:*) => (Join $right $left)";
        System.out.println(s1);

        OptPlaner optPlaner = new OptPlaner(Collections.singletonMap("test", s1),Collections.emptyMap(),Collections.emptyMap());
        FunNode root = new FunNode(null, "root", "");

        FunNode leftJoin = new FunNode(root, "Join", "");
        leftJoin.add(new FunNode(leftJoin, "left", ""));
        leftJoin.add(new FunNode(leftJoin, "right", ""));

        FunNode rightJoin = new FunNode(root, "Join", "");
        rightJoin.add(new FunNode(leftJoin, "left", ""));
        rightJoin.add(new FunNode(leftJoin, "right", ""));
        root.add(leftJoin);
        root.add(rightJoin);

        Node opt = optPlaner.opt(root);
        Assert.assertEquals("root(Join(right(),left()),Join(right(),left()))",opt.toString());
    }
    @Test
    public void test2(){
        String s1 = "define Select {Left  Expr" +
                " Right Expr }\n" +
                "[CommuteJoin]\n(Join $left:* $right:*) => (Join $right $left)";
        System.out.println(s1);

        OptPlaner optPlaner = new OptPlaner(Collections.singletonMap("test", s1),Collections.emptyMap(),Collections.emptyMap());
        FunNode root = new FunNode(null, "root", "");

        FunNode leftJoin = new FunNode(root, "Join", "");
        leftJoin.add(new FunNode(leftJoin, "left", ""));
        leftJoin.add(new FunNode(leftJoin, "right", ""));

        FunNode rightJoin = new FunNode(root, "Join", "");
        rightJoin.add(new FunNode(leftJoin, "left", ""));
        rightJoin.add(new FunNode(leftJoin, "right", ""));
        root.add(leftJoin);
        root.add(rightJoin);

        Node opt = optPlaner.opt(root);
        Assert.assertEquals("root(Join(right(),left()),Join(right(),left()))",opt.toString());
    }

}
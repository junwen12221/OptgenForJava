package cn.lightfish.optgen;

import cn.lightfish.optgen.gen.node.FunNode;
import org.junit.Test;

import java.util.Collections;

public class ReplaceTest {

    @Test
    public void test() {
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

        OptPlaner test = new OptPlaner(Collections.singletonMap("test", s1),Collections.emptyMap());
        Replacer replacer = test.getReplacer();

        FunNode root = new FunNode(null, "root", "");

        FunNode leftJoin = new FunNode(root, "Join", "");
        leftJoin.add(new FunNode(leftJoin, "left", ""));
        leftJoin.add(new FunNode(leftJoin, "right", ""));

        FunNode rightJoin = new FunNode(root, "Join", "");
        rightJoin.add(new FunNode(leftJoin, "left", ""));
        rightJoin.add(new FunNode(leftJoin, "right", ""));
        root.add(leftJoin);
        root.add(rightJoin);
        System.out.println(root);

        Estimater estimater = new Estimater(Collections.emptyMap());
        replacer.replace(root);
        System.out.println();
        double estimate = estimater.estimate(root);

    }

}
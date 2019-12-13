package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.RuleSetExpr;
import cn.lightfish.optgen.gen.Node;
import cn.lightfish.optgen.gen.node.FunNode;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

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
        Map<String, String> map = Collections.singletonMap("test", s1);
        Compiler c = Compiler.createFromText(map);
        c.setFileResolver(s -> s1);

        CompiledExpr complied = c.complie();
        Map<String, RuleSetExpr> matchIndex = complied.getMatchIndex();
        RuleSetExpr rules = complied.getRules();
        VisitorImpl visitor = new VisitorImpl();
        Replacer replacer = rules.accept(visitor);
        FunNode root = new FunNode(null,"root","");

        FunNode leftJoin = new FunNode(root, "Join", "");
        leftJoin.add(new FunNode(leftJoin,"left",""));
        leftJoin.add(new FunNode(leftJoin,"right",""));

        FunNode rightJoin = new FunNode(root, "Join", "");
        rightJoin.add(new FunNode(leftJoin,"left",""));
        rightJoin.add(new FunNode(leftJoin,"right",""));
        root.add(leftJoin);
        root.add(rightJoin);
        System.out.println(root);
        System.out.println(replacer.replace(root));

    }
}
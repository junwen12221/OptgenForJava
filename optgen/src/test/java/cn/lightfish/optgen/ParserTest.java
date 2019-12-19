package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.RootExpr;
import sun.applet.Main;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static cn.lightfish.optgen.Parser.createFromText;
import static org.junit.Assert.*;

public class ParserTest {
    public static void main(String[] args) throws Exception {
        URL resource = Main. class.getResource("/testdata/");
        Path dir = Paths.get(resource.toURI());
        Path arg = dir.resolve("parser");
        String text = "define Lt {\n" +
                "    # This is a field comment.\n" +
                "    #\n" +
                "    Left  Expr\n" +
                "\n" +
                "    # And another field comment.\n" +
                "    Right Expr\n" +
                "}";
        List<String> strings = Arrays.asList(text);
        Parser parser = createFromText(Collections.singletonMap("a",text));
        RootExpr parse = parser.parse();
        String s = parse.toString();
        System.out.println(s);

        scan();
    }

    private static void scan() throws Exception {
        URL resource = Main. class.getResource("/testdata/scanner");
        Path path = Paths.get(resource.toURI());
        Scanner scanner = new Scanner(new String( Files.readAllBytes(path)));
        for (;;){
            Token scan = scanner.scan();
            if (scan == Token.EOF){
                break;
            }else {
                System.out.println(scanner.lineLocation());
                System.out.println(scanner.literal());
            }
        }
    }

}
package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.RootExpr;
import cn.lightfish.optgen.testutils.CmdArg;
import cn.lightfish.optgen.testutils.TestData;
import cn.lightfish.optgen.testutils.TestDataReader;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import lombok.SneakyThrows;
import org.junit.Assert;
import sun.applet.Main;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class TestParser {
    public static void main(String[] args) {
        test();
    }
    @SneakyThrows
    public static void test() {
        URL resource = Main. class.getResource("/testdata/parser");
        Path dir = Paths.get(resource.toURI());
        TestDataReader.runTest(dir.toAbsolutePath().toString(),
                new Function<TestData, String>() {
                    @Override
                    public String apply(TestData testData) {
                        if (!"parse".equals(testData.getCmd())){
                            Assert.fail();
                        }
                        String name = "test.opt";
                        Map<String, String> map = Collections.singletonMap(name, testData.getInput());

                        Parser fromFileName = Parser.createFromText(map);
                        fromFileName.setFileResolver(s -> {
                            return testData.getInput();
                        });
                        RootExpr parse = fromFileName.parse();
                        if (parse!=null) {
                            return parse.toString() + "\n";
                        }else {
                            StringBuilder actual = new StringBuilder();
                            for (String error : fromFileName.errors()) {
                                actual.append(error).append('\n');
                            }
                            return actual.toString();
                        }
                    }
                });
    }
}
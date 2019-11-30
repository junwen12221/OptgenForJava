package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.RootExpr;
import cn.lightfish.optgen.testutils.CmdArg;
import cn.lightfish.optgen.testutils.TestData;
import cn.lightfish.optgen.testutils.TestDataReader;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import lombok.SneakyThrows;
import org.junit.Assert;

import java.io.Reader;
import java.io.StringReader;
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
        TestDataReader.runTest("D:\\git\\OptgenForJava\\optgen\\src\\test\\resources\\testdata\\parser",
                new Function<TestData, String>() {
                    @Override
                    public String apply(TestData testData) {
                        if (!"parse".equals(testData.getCmd())){
                            Assert.fail();
                        }
                        String name = "test.opt";
                        Map<String, String> map = Collections.singletonMap(name, testData.getInput());

                        Parser fromFileName = Parser.createFromText(map);
                        fromFileName.setFileResolver(new Function<String, Reader>() {
                            @Override
                            public Reader apply(String s) {
                                return new StringReader( testData.getInput());
                            }
                        });
                        RootExpr parse = fromFileName.parse();
                        return parse.toString()+"\n";
                    }
                });
    }
}
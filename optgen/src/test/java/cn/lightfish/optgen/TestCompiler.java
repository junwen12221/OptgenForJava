package cn.lightfish.optgen;

import cn.lightfish.optgen.testutils.TestData;
import cn.lightfish.optgen.testutils.TestDataReader;
import sun.applet.Main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class TestCompiler {
    public static void main(String[] args) throws Exception {
        URL resource = Main. class.getResource("/testdata/compiler");
        Path dir = Paths.get(resource.toURI());
        TestDataReader.runTest(dir.toAbsolutePath().toString(),
                new Function<TestData, String>() {
                    @Override
                    public String apply(TestData testData) {
                        String name = "test.opt";
                        Map<String, String> map = Collections.singletonMap(name, testData.getInput());
                        Compiler c = Compiler.createFromText(map);
                        c.setFileResolver(s -> {
                            return testData.getInput();
                        });
                        CompiledExpr complied = c.complie();
                        if (complied!=null){
                            System.out.println("------------------------------------------");
                            return complied.toString();

                        }else {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String error : c.errors) {
                                stringBuilder.append(error).append('\n');
                            }
                            return stringBuilder.toString();
                        }
                    }
                });
        }
}
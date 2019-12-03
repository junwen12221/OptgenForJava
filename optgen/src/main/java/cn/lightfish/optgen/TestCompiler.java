package cn.lightfish.optgen;

import cn.lightfish.optgen.testutils.TestData;
import cn.lightfish.optgen.testutils.TestDataReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class TestCompiler {
    public static void main(String[] args) throws IOException {
        TestDataReader.runTest("D:\\git\\OptgenForJava\\optgen\\src\\test\\resources\\testdata\\compiler",
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
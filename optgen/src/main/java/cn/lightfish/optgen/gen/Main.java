package cn.lightfish.optgen.gen;

import cn.lightfish.optgen.CompiledExpr;
import cn.lightfish.optgen.Compiler;
import cn.lightfish.optgen.VisitorImpl;
import cn.lightfish.optgen.ast.DefineSetExpr;
import cn.lightfish.optgen.ast.RuleSetExpr;
import cn.lightfish.optgen.testutils.TestData;
import cn.lightfish.optgen.testutils.TestDataReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class Main {
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
                            Map<String, RuleSetExpr> matchIndex = complied.getMatchIndex();
                            RuleSetExpr rules = complied.getRules();
                            VisitorImpl visitor = new VisitorImpl();
                            Matcher accept = rules.accept(visitor);
                            if (complied!=null){
                                System.out.println("------------------------------------------");
                                return complied.toString();

                            }else {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (String error : c.getErrors()) {
                                    stringBuilder.append(error).append('\n');
                                }
                                return stringBuilder.toString();
                            }
                        }
                    });
        }
}
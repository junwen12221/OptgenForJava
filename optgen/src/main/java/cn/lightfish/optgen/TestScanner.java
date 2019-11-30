package cn.lightfish.optgen;

import cn.lightfish.optgen.testutils.CmdArg;
import cn.lightfish.optgen.testutils.TestData;
import cn.lightfish.optgen.testutils.TestDataReader;
import lombok.SneakyThrows;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.function.Function;

public class TestScanner {
    public static void main(String[] args) {
        test();
    }
    @SneakyThrows
    public static void test() {
        TestDataReader.runTest("D:\\git\\OptgenForJava\\optgen\\src\\test\\resources\\testdata\\scanner",
                new Function<TestData, String>() {
                    @Override
                    public String apply(TestData testData) {
                        if (!"scan".equals(testData.getCmd())){
                            Assert.fail();
                        }
                        int count  = -1;
                        for (CmdArg cmdArg : testData.getCmdArgs()) {
                            if (!"fail".equals(cmdArg.getKey())||cmdArg.getVals().size()!=1){
                                Assert.fail();
                            }
                            count = Integer.parseInt( cmdArg.getVals().get(0));
                        }
                        StringReader bufferedReader = null;
                        bufferedReader  = new StringReader(testData.getInput());

                        Scanner scanner;
                        if (count!=-1){
                            scanner =  new Scanner(new ErrorReader(bufferedReader,count));
                        }else {
                            scanner = new Scanner(bufferedReader);
                        }

                        StringBuilder buffer = new StringBuilder();
                        for (;;){
                            Token token = scanner.scan();
                            if (token==Token.EOF){
                                break;
                            }
                            buffer.append(MessageFormat.format("({0} {1})\n", token, scanner.literal()));
                            if (token==Token.ERROR){
                                break;
                            }
                        }


                        return buffer.toString();
                    }
                });
    }
    static class ErrorReader extends Reader {
        Reader r;
        int count;

        public ErrorReader(Reader r, int count) {
            this.r = r;
            this.count = count;
        }

        @Override
        public void close() throws IOException {
            r.close();
        }

        @Override
        public int read() throws IOException {
            count--;
            if (count <=0){
                return -1;
            }
            return r.read();
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return 0;
        }
    }
}
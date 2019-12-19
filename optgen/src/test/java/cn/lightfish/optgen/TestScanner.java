package cn.lightfish.optgen;

import cn.lightfish.optgen.testutils.CmdArg;
import cn.lightfish.optgen.testutils.TestData;
import cn.lightfish.optgen.testutils.TestDataReader;
import lombok.SneakyThrows;
import org.junit.Assert;
import sun.applet.Main;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.function.Function;

public class TestScanner {
    public static void main(String[] args) {
        test();
    }
    @SneakyThrows
    public static void test() {
        URL resource = Main. class.getResource("/testdata/scanner");
        Path dir = Paths.get(resource.toURI());
        TestDataReader.runTest(dir.toAbsolutePath().toString(),
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


                        Scanner scanner;
                        if (count!=-1){
                            scanner =  new Scanner(testData.getInput(),count);
                        }else {
                            scanner = new Scanner(testData.getInput());
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
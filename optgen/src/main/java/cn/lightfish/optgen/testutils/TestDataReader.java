package cn.lightfish.optgen.testutils;

import lombok.SneakyThrows;
import org.junit.Assert;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestDataReader {
    private StringBuilder rewrite;
    private final LineIterator iterator;
    private String file;
    private boolean verbose;

    private TestData data = new TestData();

    public TestDataReader(String path) {
        this(path, false, false);
    }

    @SneakyThrows
    public TestDataReader(String path, boolean rewriteTestFiles, boolean verbose) {
        this.file = path;
        this.iterator = new LineIterator(new LineNumberReader(Files.newBufferedReader(Paths.get(path))));
        if (rewriteTestFiles) {
            rewrite = new StringBuilder();
        }
        this.verbose = verbose;
    }

    public void readExpected() {
        StringBuilder buf = new StringBuilder();
        String line = "";
        boolean allowBlankLines = false;

        if (iterator.hasNext()) {
            line = iterator.next();
            if ("----".equals(line)) {
                allowBlankLines = true;
            }
        }
        if (allowBlankLines) {
            while (iterator.hasNext()) {
                line = iterator.next();
                if ("----".equals(line)) {
                    if (iterator.hasNext()) {
                        String line2 = iterator.next();
                        if ("----".equals(line2)) {
                            break;
                        }
                        buf.append(line).append('\n');
                        buf.append(line2).append('\n');
                        continue;
                    }
                }
                buf.append(line).append('\n');;
            }
        } else {
            while (true) {
                if ("".equals(line.trim())) {
                    break;
                }
                buf.append(line).append('\n');;

                if (!iterator.hasNext()) {
                    break;
                }
                line = iterator.next();
            }
            data.expected = buf.toString();
        }
    }

    public boolean next() {
        Iterator<String> iterator = this.iterator;
        this.data = new TestData();
        while (iterator.hasNext()) {
            String line = iterator.next();
            emit(line);
            if (line.trim().startsWith("#")) {
                continue;
            }
            while (line.startsWith("\\") && iterator.hasNext()) {
                String nextLine = iterator.next();
                emit(nextLine);
                line = trimSuffix(line, "\\") + " " + nextLine.trim();
            }

            List<String> fields = splitDirectives(line);
            if (fields.isEmpty()) {
                continue;
            }
            data.cmd = fields.get(0);
            data.pos = MessageFormat.format("{0}:{1}", this.file, this.iterator.getLineNumber());

            for (String arg : fields.subList(1, fields.size())) {
                String key = arg;
                int pos = arg.indexOf("=");
                List<String> vals = Collections.emptyList();
                if (pos >= 0) {
                    key = arg.substring(0, pos);
                    String val = arg.substring(pos + 1);

                    if (val.length() > 2 && val.charAt(0) == '(' && val.charAt(val.length() - 1) == ')') {
                        vals = Arrays.asList(val.substring(1, val.length() - 1).split(","));
                        vals = vals.stream().map(i -> i.trim()).collect(Collectors.toList());
                    } else {
                        vals = new ArrayList<>();
                        vals.add(val);
                    }
                }
                data.cmdArgs.add(new CmdArg(key, vals));
            }

            boolean separator = false;
            StringBuilder stringBuilder = new StringBuilder();
            while (iterator.hasNext()) {
                line = iterator.next();
                if ("----".equals(line)) {
                    separator = true;
                    break;
                }
                emit(line);
                stringBuilder.append(line).append('\n');
            }
            data.input = stringBuilder.toString().trim();

            if (separator) {
                readExpected();
            }
            return true;

        }
        return false;
    }

    static final Pattern splitDirectivesRE = Pattern.compile("^ *[a-zA-Z0-9_,-\\.]+(|=[-a-zA-Z0-9_@]+|=\\([^)]*\\))( |$)");

    public static void main(String[] args) throws IOException {

        Function<TestData, String> fun = new Function<TestData, String>() {
            @Override
            public String apply(TestData testData) {
                return null;
            }
        };
        String path = "D:\\git\\OptgenForJava\\optgen\\src\\test\\resources\\testdata\\scanner";
        runTest(path,fun);

    }

    public static void runTest(String path,Function<TestData, String> fun) throws IOException {
        TestDataReader testDataReader = new TestDataReader(path);
        while (testDataReader.next()) {
            TestData data = testDataReader.data();
            String actual = "";
            try {
                actual = fun.apply(data);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(String.format("\npanic during %s:\n%s\n", data.getPos(), data.getInput()));

            }
            if (testDataReader.rewrite != null) {
                testDataReader.emit("----");
                if (hasBlankLine(actual)) {
                    testDataReader.emit("----");
                    testDataReader.rewrite.append(actual);
                    testDataReader.emit("----");
                    testDataReader.emit("----");
                } else {
                    testDataReader.emit(actual);
                }
            } else if (!data.expected.equals(actual)) {
                Assert.fail(String.format("\n%s: %s\nexpected:\n%s\nfound:\n%s", data.getPos(), data.getInput(), data.getExpected(), actual));
            } else if (testDataReader.verbose) {
                System.out.format("\n%s:\n%s\n----\n%s", data.getPos(), data.getInput(), actual);
            }
        }
        if (testDataReader.rewrite != null) {
            String data = testDataReader.rewrite.toString();
            int length = data.length();
            if (length > 2 && data.charAt(length - 1) == '\n' && data.charAt(length - 2) == '\n') {
                data = data.substring(0, length - 1);
            }
            try {
                Files.write(Paths.get(testDataReader.file), data.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
    }

    private static List<String> splitDirectives(String line) {
        List<String> res = new ArrayList<>();

        while (true) {
            Matcher matcher = splitDirectivesRE.matcher(line);
            if (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                res.add(line.substring(start, end).trim());
                line = line.substring(end);
            } else {
                break;
            }
        }
        if (res.isEmpty()) {
            Assert.fail("cannot parse directive " + line + "\n");
        }
        return res;
    }

    private void emit(String nextLine) {
        if (rewrite != null) {
            rewrite.append(nextLine).append('\n');
        }
    }


    private static String trimSuffix(String line, String s) {
        if (line.endsWith(s)) {
            return line.substring(0, line.length() - s.length());
        } else {
            return line;
        }
    }

    public TestData data() {
        return data;
    }

    public static boolean hasBlankLine(String s) throws IOException {
        StringReader stringReader = new StringReader(s);
        LineNumberReader lineNumberReader = new LineNumberReader(stringReader);
        String s1 = lineNumberReader.readLine();
        if ("".equals(s1.trim())) {
            return true;
        } else {
            return false;
        }
    }
}
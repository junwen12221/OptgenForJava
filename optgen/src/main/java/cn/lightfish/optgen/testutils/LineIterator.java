package cn.lightfish.optgen.testutils;

import java.io.Closeable;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineIterator implements   Iterator<String> , Closeable {
    String nextLine = null;
    int lineNumber;
    LineNumberReader lineNumberReader;

    public String getLine() {
        return nextLine;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public LineIterator(LineNumberReader lineNumberReader) {
        this.lineNumberReader = lineNumberReader;
    }

    @Override
        public boolean hasNext() {
            if (nextLine != null) {
                return true;
            } else {
                try {
                    nextLine = lineNumberReader.readLine();
                    lineNumber = lineNumberReader.getLineNumber();
                    return (nextLine != null);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        @Override
        public String next() {
            if (nextLine != null || hasNext()) {
                String line = nextLine;
                nextLine = null;
                return line;
            } else {
                throw new NoSuchElementException();
            }
        }

    @Override
    public void close() throws IOException {
        lineNumberReader.close();
    }
}
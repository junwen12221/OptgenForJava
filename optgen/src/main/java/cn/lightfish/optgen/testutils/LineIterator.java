// Copyright 2018 The Cockroach Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied. See the License for the specific language governing
// permissions and limitations under the License.
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
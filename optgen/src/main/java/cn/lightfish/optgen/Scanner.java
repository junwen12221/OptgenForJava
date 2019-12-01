package cn.lightfish.optgen;

import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;

public class Scanner implements Closeable {
    private MyReader reader;
    private Token token;
    private String literal;
    private int line;
    private int pos;
    private int prev;


    public Scanner(String input, int count) {
        this.reader = new MyReader(input, count);
    }

    public Scanner(String input) {
        this.reader = new MyReader(input);
    }

    public Token token() {
        return token;
    }

    public LineLocation lineLocation() {
        return new LineLocation(line, pos, prev);
    }

    public String error() {
        return reader.getError();
    }


    static class MyReader {
        String input;
        int index = 0;
        int count;
        private String error;
        public MyReader(String input) {
            this.input = input;
            this.count = Integer.MAX_VALUE;
        }

        public MyReader(String input, int count) {
            this.input = input;
            this.count = count;
        }


        public int read() {
            count--;
            if (count <= 0) {
                return -1;
            }
            return inner();
        }

        public String getError() {
            return error;
        }
        private int inner() {
            if (index < input.length()) {
                return input.charAt(index++);
            }
            if (index == input.length()) {
                index++;
                return 0;
            }
            if (count == Integer.MAX_VALUE) {
                error = "read/write on closed pipe";
                return -1;
            }else {
                return 0;
            }
        }

        public void unread() {
            ++count;
            --index;
        }
    }

    @SneakyThrows
    public Token scan() {
            int c = read();

            if (Character.isSpaceChar(c) || Character.isWhitespace(c)) {
                unread();
                return scanWhitespace();
            }
            if (Character.isLetter(c) || c == '_') {
                unread();
                return scanIdentifier();
            }
            if (Character.isDigit(c)) {
                unread();
                return scanNumericLiteral();
            }
            switch (c) {
                case -1: {
                    token = Token.ERROR;
                    literal = error();
                    break;
                }
                case 0: {
                    token = Token.EOF;
                    literal = "";
                    break;
                }
                case '(': {
                    token = Token.LPAREN;
                    literal = "(";
                    break;
                }
                case ')': {
                    token = Token.RPAREN;
                    literal = ")";
                    break;
                }
                case '[': {
                    token = Token.LBRACKET;
                    literal = "[";
                    break;
                }
                case ']': {
                    token = Token.RBRACKET;
                    literal = "]";
                    break;
                }
                case '{': {
                    token = Token.LBRACE;
                    literal = "{";
                    break;
                }
                case '}': {
                    token = Token.RBRACE;
                    literal = "}";
                    break;
                }
                case '$': {
                    token = Token.DOLLAR;
                    literal = "$";
                    break;
                }
                case ':': {
                    token = Token.COLON;
                    literal = ":";
                    break;
                }
                case '*': {
                    token = Token.ASTERISK;
                    literal = "*";
                    break;
                }
                case ',': {
                    token = Token.COMMA;
                    literal = ",";
                    break;
                }
                case '^': {
                    token = Token.CARET;
                    literal = "^";
                    break;
                }
                case '|': {
                    token = Token.PIPE;
                    literal = "|";
                    break;
                }
                case '&': {
                    token = Token.AMPERSAND;
                    literal = "&";
                    break;
                }
                case '=': {
                    if (read() == '>') {
                        token = Token.ARROW;
                        literal = "=>";
                        break;
                    }
                    unread();
                    token = Token.EQUALS;
                    literal = "=";
                    break;
                }
                case '.': {
                    if (read() == '.') {
                        if (read() == '.') {
                            token = Token.ELLIPSES;
                            literal = "...";
                            break;
                        }
                    }
                    token = Token.ILLEGAL;
                    literal = ".";
                    break;
                }
                case '"': {
                    unread();
                    return scanStringLiteral();
                }
                case '#': {
                    unread();
                    return scanComment();
                }
                default:
                    token = Token.ILLEGAL;
                    literal = String.valueOf(c);
            }
            return token;

    }

    @SneakyThrows
    private int read() {
        if (error()!=null){
            return -1;
        }
        int c = (int) reader.read();
        if (error()!=null){
            return -1;
        }
        if (c == -1 || c == Character.MAX_VALUE) {
            return -1;
        }
        if (c == 0) {

            return 0;
        }
        prev = pos;
        if (c == '\n') {
            line++;
            pos = 0;
        } else {
            pos++;
        }
        return (char) c;
    }

    @SneakyThrows
    private void unread() {
        reader.unread();
        token = Token.ILLEGAL;
        literal = "";
        if (pos == 0) {
            line--;
        }
        pos = prev;
        prev = -1;
    }

    private Token scanStringLiteral() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) read());
        int c;
        do {
            c = read();
            if (c == 0 || c == -1 || c == '\n') {
                unread();
                token = Token.ILLEGAL;
                break;
            }
            stringBuilder.append((char) c);
            if (c == '"') {
                token = Token.STRING;
                break;
            }
        } while (true);
        literal = stringBuilder.toString();
        return token;
    }

    private Token scanComment() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) read());
        int c;
        do {
            c = read();
            if (c == 0 || c == -1 || c == '\n') {
                break;
            }
            stringBuilder.append((char) c);
        } while (true);
        unread();
        token = Token.COMMENT;
        literal = stringBuilder.toString();
        return Token.COMMENT;
    }

    private Token scanIdentifier() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) read());
        int c;
        do {
            c = read();
            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_') {
                break;
            }
            stringBuilder.append((char) c);
        } while (true);
        unread();
        token = Token.IDENT;
        literal = stringBuilder.toString();
        return Token.IDENT;
    }

    private Token scanNumericLiteral() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) read());
        int c;
        do {
            c = read();
            if (!Character.isDigit(c)) {
                break;
            }
            stringBuilder.append((char) c);
        } while (true);
        unread();
        token = Token.NUMBER;
        literal = stringBuilder.toString();
        return Token.NUMBER;
    }

    private Token scanWhitespace() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) read());
        int c;
        do {
            c = read();
            if (!(Character.isSpaceChar(c) || Character.isWhitespace(c))) {
                break;
            }
            stringBuilder.append((char) c);
        } while (true);
        unread();
        token = Token.WHITESPACE;
        literal = stringBuilder.toString();
        return Token.WHITESPACE;
    }

    public String literal() {
        return literal;
    }

    @Override
    public void close() throws IOException {

    }
}
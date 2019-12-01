package cn.lightfish.optgen;

import cn.lightfish.optgen.ast.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.lightfish.optgen.Token.*;

public class Parser {
    private List<String> files;
    private int fileIndex;

    private SourceLoc src;
    private Scanner scanner;
    private final List<String> errors = new ArrayList<>();
    private CommentsExpr comments;
    private Function<String, String> resolver;
    private String reader;
    private boolean unscanned;
    private SourceLoc saveSrc;

    private Parser(List<String> files, Function<String, String> resolver) {
        this.files = files;
        this.resolver = resolver;
    }

    public static Parser createFromText(Map<String,String> list){
        List<String> key = new ArrayList<>(list.keySet());
        return new Parser(key, (s) -> {
            return list.get(s);
        });
    }
    public static Parser createFromFileName(List<String> list){
        return new Parser(list, (s) -> {
            try {
                return new String( Files.readAllBytes(Paths.get(s)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void setFileResolver(Function<String, String> resolver) {
        this.resolver = resolver;
    }

    public SourceLoc src() {
        return new SourceLoc(src.file, src.pos, src.line);
    }

    public RootExpr parse() {
        RootExpr root = parseRoot();
        closeScanner();
        return root;
    }

    private boolean openScanner() {
        try {
            this.reader = resolver.apply(files.get(fileIndex));
            closeScanner();
            this.scanner = new Scanner(this.reader);
            this.src = new SourceLoc(null,0,0);
            this.src.file = files.get(fileIndex);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            addError(e.getLocalizedMessage());
            return false;
        }
    }

    @SneakyThrows
    private void closeScanner() {
        if (scanner != null) {
            scanner.close();
            this.reader = null;
            this.scanner = null;
        }
    }

    private RootExpr parseRoot() {
        RootExpr rootExpr = new RootExpr();
        if (scanner == null) {
            if (files.isEmpty()) {
                return rootExpr;
            }
            if (!openScanner()) {
                return null;
            }
        }
        for (; ; ) {
            CommentsExpr comments=new CommentsExpr();
            TagsExpr tags = new TagsExpr();

            this.comments = new CommentsExpr();

            Token token = scan();
            SourceLoc src = src();

            switch (token) {
                case EOF:
                    return rootExpr;
                case LBRACKET: {
                    unscan();
                    comments = this.comments;
                    this.comments = null;

                    tags = parseTags();
                    if (tags == null) {
                        tryRecover();
                        break;
                    }

                    if (scan() != IDENT) {
                        unscan();
                        RuleExpr rule = parseRule(comments, tags, src);
                        if (rule == null) {
                            tryRecover();
                            break;
                        }
                        rootExpr.appendRule(rule);
                        break;
                    }
                    throw new UnsupportedOperationException();
                }
                case IDENT: {
                    if (comments == null) {
                        comments = this.comments;
                        this.comments = null;
                    }
                    if (!isDefinedIdent()) {
                        addExpectedTokenErr("define statement");
                        tryRecover();
                        break;
                    }
                    unscan();
                    DefineExpr define = parseDefine(comments, tags, src);
                    if (define == null) {
                        tryRecover();
                        break;
                    }
                    rootExpr.append(define);
                    break;
                }
                default:
                    addExpectedTokenErr("define statement or rule");
                    tryRecover();
            }
        }
    }

    private DefineExpr parseDefine(CommentsExpr comments, TagsExpr tags, SourceLoc src) {
        if (!scanToken(IDENT, "define statement") || !"define".equals(scanner.literal())) {
            return null;
        }
        if (!scanToken(IDENT, "define name")) {
            return null;
        }
        String name = scanner.literal();
        DefineExpr defineExpr = new DefineExpr(src, comments, new StringExpr(name), tags);
        if (!scanToken(LBRACE, "'{'")) {
            return null;
        }
        for (; ; ) {
            this.comments = new CommentsExpr();

            if (scan() == RBRACE) {
                return defineExpr;
            }
            unscan();

            CommentsExpr newComments = this.comments;
            this.comments = null;
            DefineFieldExpr defineField = parseDefineField(newComments);
            if (defineField == null) {
                return null;
            }
            defineExpr.append(defineField);
        }
    }

    private DefineFieldExpr parseDefineField(CommentsExpr comments) {
        if (!scanToken(IDENT, "define field name")) {
            return null;
        }
        SourceLoc src = src();
        String name = scanner.literal();
        if (!scanToken(IDENT, "define field type")) {
            return null;
        }
        String type = scanner.literal();
        return new DefineFieldExpr(src, new StringExpr(name), comments, new StringExpr(type));
    }

    private Expr parseExpr() {
        Token t = scan();
        switch (t) {
            case LPAREN: {
                unscan();
                return parseFunc();
            }
            case CARET: {
                unscan();
                return parseNot();
            }
            case LBRACKET: {
                unscan();
                return parseList();
            }
            case ASTERISK: {
                return new AnyExpr(this.src());
            }
            case IDENT: {
                return new NameExpr(scanner.literal());
            }
            case STRING: {
                unscan();
                return parseString();
            }
            case NUMBER: {
                unscan();
                return parseNumber();
            }
            default:
                addExpectedTokenErr("expression");
        }
        return null;
    }

    private void addExpectedTokenErr(String desc) {
        if (scanner.token() == Token.EOF) {
            addError(MessageFormat.format("expected {0},found EOF", desc));
        } else {
            addError(MessageFormat.format("expected {0},found {1}", desc, scanner.literal()));
        }
    }

    private boolean scanToken(Token expected, String desc) {
        Token scan = scan();
        if (scan!= expected) {
            addExpectedTokenErr(desc);
            return false;
        }
        return true;
    }

    private Expr parseNumber() {
        if (scan() != Token.NUMBER) {
            panic("caller should have checked for numeric literal");
        }
        try {
            return new NumberExpr(Long.parseLong(scanner.literal()));
        } catch (Exception e) {
            addError(e.getLocalizedMessage());
            return null;
        }
    }

    private Expr parseString() {
        if (scan() != STRING) {
            panic("caller should have checked for literal string");
        }
        String literal = this.scanner.literal();
        return new StringExpr(literal.substring(1, literal.length() - 1));
    }


    private RuleExpr parseRule(CommentsExpr comments, TagsExpr tags, SourceLoc src) {
        Expr match = parseMatch();
        if (match == null) {
            return null;
        }
        if (scanToken(Token.ARROW, "'=>'")) {
            return null;
        }
        Expr replace = parseReplace();
        if (replace == null) {
            return null;
        }
        TagsExpr newTags = new TagsExpr();
        int count = tags.childCount();
        for (int i = 1; i <count ; i++) {
            newTags.append(tags.child(i));
        }

        return new RuleExpr(src, new StringExpr(tags.child(0).value()), comments, newTags, (FuncExpr) match, replace);
    }

    private Expr parseMatch() {
        if (!scanToken(Token.LPAREN, "match pattern")) {
            return null;
        }
        unscan();
        return parseFunc();
    }

    private Expr parseReplace() {
        switch (scan()) {
            case LPAREN: {
                unscan();
                return parseFunc();
            }
            case DOLLAR: {
                unscan();
                return parseRef();
            }
            default: {
                addExpectedTokenErr("replace pattern");
                return null;
            }
        }
    }

    private Expr parseList() {
        if (scan() != Token.LBRACKET) {
            panic("caller should have checked for left bracket");
        }
        SourceLoc src = src();
        ListExpr list = new ListExpr(src);
        for (; ; ) {
            if (scan() == Token.RBRACKET) {
                return list;
            }
            unscan();
            Expr item = parseListChild();
            if (item == null) {
                return null;
            }
            list.append(item);
        }
    }

    private Expr parseListChild() {
        if (scan() == Token.ELLIPSES) {
            return new ListAnyExpr(src());
        }
        unscan();
        return parseArg();
    }

    private TagsExpr parseTags() {
        TagsExpr tags = new TagsExpr();
        if (scan() != Token.LBRACKET) {
            panic("caller should have checked for left bracket");
        }
        for (; ; ) {
            if (!scanToken(Token.IDENT, "tag name")) {
                return null;
            }
            tags.append(new TagExpr(scanner.literal()));
            if (scan() == Token.RBRACKET) {
                return tags;
            }
            unscan();
            if (!scanToken(COMMA, "comma")) {
                return null;
            }
        }
    }

    private void panic(String s) {
    }

    private Expr parseRef() {
        if (scan() != Token.DOLLAR) {
            panc("caller should have checked for dollar");
        }
        SourceLoc src = src();
        if (scanToken(Token.IDENT, "label")) {
            return null;
        }
        return new RefExpr(src, new StringExpr(scanner.literal()));
    }

    private Expr parseNot() {
        if (scan() != Token.CARET) {
            panic("caller should have checked for caret");
        }
        SourceLoc src = src();
        Expr input = parseExpr();
        if (input == null) {
            return null;
        }
        return new NotExpr(src, input);
    }

    private Expr parseFunc() {
        if (scan() != Token.LPAREN) {
            panc("caller should have checked for left parenthesis");
        }
        SourceLoc src = this.src();
        Expr name = parseFuncName();
        if (name == null) {
            return null;
        }
        FuncExpr fn = new FuncExpr(src, name);
        for (; ; ) {
            if (scan() == Token.RPAREN) {
                return fn;
            }
            unscan();
            Expr arg = parseArg();
            if (arg==null){
                return null;
            }
            fn.append(arg);
        }
    }

    private Expr parseArg() {
        Token token = scan();
        unscan();
        if (token == Token.DOLLAR) {
            return parseBindOrRef();
        }
        return parseAnd();
    }

    private Expr parseAnd() {
        SourceLoc src = peekNextSource();
        Expr left = parseExpr();
        if (left == null) {
            return null;
        }
        if (scan() != Token.AMPERSAND) {
            unscan();
            return left;
        }
        Expr right = parseAnd();
        if (right == null) {
            return null;
        }
        return new AndExpr(src, left, right);
    }

    private SourceLoc peekNextSource() {
        scan();
        SourceLoc src = this.src();
        unscan();
        return src;
    }

    private Expr parseBindOrRef() {
        if (scan() != Token.DOLLAR) {
            panc("caller should have checked for dollar");
        }
        SourceLoc src = this.src();
        if (!scanToken(Token.IDENT, "label")) {
            return null;
        }
        StringExpr label = new StringExpr( scanner.literal());
        if (scan() != Token.COLON) {
            unscan();
            return new RefExpr(src, label);
        }

        Expr target = parseAnd();
        if (target == null) {
            return null;
        }
        return new BindExpr(src, label, target);
    }

    private void panc(String s) {
        throw new RuntimeException(s);
    }

    private Expr parseFuncName() {
        switch (scan()) {
            case IDENT: {
                unscan();
                return parseNames();
            }
            case LPAREN: {
                unscan();
                return parseFunc();
            }
            default:
        }
        addExpectedTokenErr("name");
        return null;
    }

    private Expr parseNames() {
        NamesExpr names = new NamesExpr();
        for (; ; ) {
            if (scanToken(IDENT, "name")) {
                return null;
            }
            names.append(new NameExpr(scanner.literal()));
            if (scan() != PIPE) {
                unscan();
                return names;
            }
        }
    }

    private void unscan() {
        if (this.unscanned) {
            panic("unscan was already called");
        }
        SourceLoc src = src();
        SourceLoc saveSrc = saveSrc();

        this.src = saveSrc;
        this.saveSrc = src;

        this.unscanned = true;
    }

    private SourceLoc saveSrc() {
        return new SourceLoc(this.saveSrc.file,this.saveSrc.pos,this.saveSrc.line);
    }

    private Token scan() {
        if (unscanned){
            SourceLoc newSaveSrc = saveSrc();
            SourceLoc src = src();
            this.src = newSaveSrc;
            this.saveSrc = src;
            unscanned =  false;
            return scanner.token();
        }
        for (;;){
            this.saveSrc  = this.src();
            LineLocation lineLocation = scanner.lineLocation();
            this.src.line = lineLocation.line;
            this.src.pos = lineLocation.pos;

            Token token = scanner.scan();
            switch (token){
                case EOF:{
                    if (fileIndex +1>=files.size()){
                        return EOF;
                    }
                    fileIndex++;
                    if (!openScanner()){
                        return EOF;
                    }
                    break;
                }
                case ERROR:{
                    addError(scanner.literal());
                    return ERROR;
                }
                case COMMENT:{
                    if (comments!=null){
                        comments.append(new CommentExpr(scanner.literal()));
                    }
                    break;
                }
                case WHITESPACE:{
                    if (comments!=null&&scanner.literal().chars().filter(i->i=='\n').count()>1){
                        this.comments = new CommentsExpr();
                    }
                    break;
                }
                default:
                    return token;
            }
        }
    }

    void addError(String text) {
        errors.add(MessageFormat.format("{0}:{1}", src, text));
    }


    void tryRecover() {
        for (; ; ) {
            Token token = scan();
            switch (token) {
                case EOF:
                case ERROR:
                    return;
                case LBRACKET:
                case IDENT:
                    if (src.pos == 0) {
                        if (token == Token.LBRACKET || isDefinedIdent()) {
                            unscan();
                        }
                        return;
                    }
            }
        }
    }

    private boolean isDefinedIdent() {
        return scanner.token().equals(Token.IDENT) && "define".equals(scanner.literal());
    }
}
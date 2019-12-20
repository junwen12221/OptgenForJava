# OptgenForJava

Optgen language是用于转换表达式树的DSL,源于

https://github.com/cockroachdb/cockroach/tree/master/pkg/sql/opt/optgen

具体说明可以看

https://github.com/cockroachdb/cockroach/blob/master/pkg/sql/opt/optgen/lang/doc.go

词法分析完成

语法分析完成

编译(解语法糖)完成

简单的执行器,支持bind ref(待改进)



测试工具Data-Driven Tests for java

https://github.com/junwen12221/OptgenForJava/tree/master/optgen/src/main/java/cn/lightfish/optgen/testutils

Data-Driven Tests for Go

https://github.com/cockroachdb/datadriven



大概效果

```java
  String s1 = "define Join {\n" +
                "    # Left comment.\n" +
                "    Left  Expr\n" +
                "\n" +
                "    # Right comment.\n" +
                "    Right Expr\n" +
                "}\n" +
                "\n" +
                "# CommuteJoin comment.\n" +
                "[CommuteJoin]\n" +
                "(Join $left:* $right:*) => (Join $right $left)";
        System.out.println(s1);

        OptPlaner optPlaner = new OptPlaner(Collections.singletonMap("test", s1),Collections.emptyMap(),Collections.emptyMap());
        FunNode root = new FunNode(null, "root", "");

        FunNode leftJoin = new FunNode(root, "Join", "");
        leftJoin.add(new FunNode(leftJoin, "left", ""));
        leftJoin.add(new FunNode(leftJoin, "right", ""));

        FunNode rightJoin = new FunNode(root, "Join", "");
        rightJoin.add(new FunNode(leftJoin, "left", ""));
        rightJoin.add(new FunNode(leftJoin, "right", ""));
        root.add(leftJoin);
        root.add(rightJoin);

        Node opt = optPlaner.opt(root);
        Assert.assertEquals("root(Join(right(),left()),Join(right(),left()))",opt.toString());
```

https://github.com/junwen12221/OptgenForJava/blob/master/optgen/src/test/java/cn/lightfish/optgen/OptPlanerTest.java

## 基本概念



###### token

```
  STRING     = " [^"\n]* "
  NUMBER     = UnicodeDigit+
  IDENT      = (UnicodeLetter | '_') (UnicodeLetter | '_' | UnicodeNumber)*
  COMMENT    = '#' .* \n
  WHITESPACE = UnicodeSpace+
```



###### 结构定义(Definitions)

结构定义所用到的表达式节点,描述字段名字,字段类型,在编译中该定义用于检查DSL语法语义是否正确以及函数调用

```java
  define <name> {
    <field-1-name> <field-1-type>
    <field-2-name> <field-2-type>
    ...
  }
  field-name = IDENT
  field-type = IDENT
```

  field-name 



###### 定义标记(Definition Tags)

描述结构定义的属性,该属性是被执行器处理的信息

```java
  [<tag-1-name>, <tag-2-name>, ...]
  define <name> {
  }
  tags = '[' IDENT (',' IDENT)* ']'

```



###### 规则(Rules)

```java
  [<rule-name>, <tag-1-name>, <tag-2-name>, ...]
  (<match-opname>
    <match-expr>
    <match-expr>
    ...
  )
  =>
  (<replace-opname>
    <replace-expr>
    <replace-expr>
    ...
  )
  rule-name = IDENT
```

rule-name必须唯一

<match-opname是匹配的节点的名字,同时也是替换点

match-exp是匹配的条件



###### not表达式

```
  not          = '^' expr
```



###### list表达式

```
'[' list-child* ']'
 list-child   = list-any | arg
 list-any = '...'
```

 list-any表达式只能在list表达式里面出现,其实现取决于执行器(暂没有实现)



###### bind表达式

```
 bind         = '$' label ':' and
```

该表达式只能在match表达式出现

  

###### Ref表达式

```
ref          = '$' label
```

在replace中出现,与match表达式对应,获取对应的匹配节点



## 测试文件

词法测试

https://github.com/junwen12221/OptgenForJava/blob/master/optgen/src/test/java/cn/lightfish/optgen/TestScanner.java

解析器测试

https://github.com/junwen12221/OptgenForJava/blob/master/optgen/src/test/java/cn/lightfish/optgen/ParserTest.java

编译器测试

https://github.com/junwen12221/OptgenForJava/blob/master/optgen/src/test/java/cn/lightfish/optgen/TestCompiler.java

## License

Apache
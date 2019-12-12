package cn.lightfish.optgen.gen;

@FunctionalInterface
public interface Matcher {
    boolean match(Object arg);
}
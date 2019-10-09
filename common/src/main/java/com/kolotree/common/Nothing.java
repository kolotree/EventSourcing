package com.kolotree.common;

import java.util.function.Supplier;

public class Nothing {

    public static final Nothing NOTHING = new Nothing();

    @Override
    public String toString() {
        return "Nothing";
    }

    public <T> T map(Supplier<T> supplier) {
        return supplier.get();
    }

    public <T> T map(T t) {
        return t;
    }

    public Nothing andThen(Runnable runnable) {
        runnable.run();
        return this;
    }
}

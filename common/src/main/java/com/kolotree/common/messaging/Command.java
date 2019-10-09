package com.kolotree.common.messaging;

public abstract class Command implements Message {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

package com.kolotree.common;

import java.util.List;

public abstract class ValueObject {

    protected abstract List<Object> getEqualityComponents();

    @Override
    public int hashCode() {
        return getEqualityComponents().stream().mapToInt(Object::hashCode)
                .reduce(1, (current, newValue) -> current * 23 + newValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        return getEqualityComponents().equals(((ValueObject)obj).getEqualityComponents());
    }
}

package com.kolotree.common;

public abstract class Entity<T extends Id> {

    private T id;

    protected Entity(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        return getId().equals(((Entity)obj).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}

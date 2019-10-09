package com.kolotree.common.error;

import com.kolotree.common.ValueObject;

import java.util.List;

public class Error extends ValueObject {

    private String message;

    public Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return null;
    }
}

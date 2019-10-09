package com.kolotree.common.exception;

public class ArgumentEmptyException extends RuntimeException {

    public ArgumentEmptyException(String argumentName) {
        super(argumentName + " can't be empty");
    }
}

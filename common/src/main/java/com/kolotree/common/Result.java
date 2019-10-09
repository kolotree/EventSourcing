package com.kolotree.common;

import com.kolotree.common.error.Error;
import io.vavr.CheckedFunction0;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.kolotree.common.Nothing.NOTHING;

public class Result<T> {

    public static Result<Nothing> ok() {
        return new Result<>(Either.right(NOTHING));
    }

    public static <T> Result<T> ok(T result) {
        if (result == null) throw new IllegalArgumentException("result can't be null");
        return new Result<>(Either.right(result));
    }

    public static <T> Result<T> fail(String errorMessage) {
        if (errorMessage == null) throw new IllegalArgumentException("errorMessage can't be null");
        return fail(new Error(errorMessage));
    }

    public static <T> Result<T> fail(Error error) {
        if (error == null) throw new IllegalArgumentException("error can't be null");
        return new Result<>(Either.left(error));
    }

    public static <T> Result<List<T>> flatten(Result<T>... results) {
        return flatten(Arrays.asList(results));
    }

    public static <T> Result<List<T>> flatten(List<Result<T>> results) {
        Iterator<Result<T>> iter = results.iterator();
        StringBuilder errors = new StringBuilder();
        List<T> values = new LinkedList<>();
        while (iter.hasNext()) {
            Result<T> value = iter.next();
            value.onFailure(error -> errors.append(error.getMessage()).append(System.lineSeparator()));
            value.onSuccess(values::add);
        }
        return errors.length() == 0 ? ok(values) : fail(new Error(errors.toString().trim()));
    }

    public static <T> Result<T> from(CheckedFunction0<T> checkedFunction) {
        return Try.of(checkedFunction).map(Result::ok).getOrElseGet(throwable -> Result.fail(throwable.getMessage()));
    }

    public Result(Either<Error, T> either) {
        this.either = either;
    }

    public boolean isSuccess() { return either.isRight(); }

    public boolean isFailure() { return either.isLeft(); }

    public <K> Result<K> map(Function<T, K> mapper) {
        return new Result<>(either.map(mapper));
    }

    public <K> Result<K> flatMap(Function<T, Result<K>> mapper) {
        return either.fold(Result::fail, mapper::apply);
    }

    public Result<Nothing> toNothing() { return new Result<>(either.map(t -> NOTHING)); }

    public Result<T> onBoth(Consumer<Result<T>> consumer) {
        either.peek(t -> consumer.accept(this)).peekLeft(error -> consumer.accept(this));
        return this;
    }

    public Result<T> onFailure(Consumer<Error> consumer) {
        either.peekLeft(consumer);
        return this;
    }

    public Result<T> onSuccess(Consumer<T> consumer) {
        either.peek(consumer);
        return this;
    }

    public T get() {
        if (isSuccess()) return either.get();
        throw new UnsupportedOperationException("Result is failure");
    }

    public Error getError() {
        if (isFailure()) return either.getLeft();
        throw new UnsupportedOperationException("Result is success");
    }

    public Result<T> onFailRetryWith(Supplier<Result<T>> retryCallback) {
        if (isSuccess()) return this;
        return retryCallback.get();
    }

    public <T2> Result<Tuple2<T, T2>> mergeWith(Result<T2> other) {
        return this.flatMap(t -> other.map(t2 -> new Tuple2<>(t, t2)));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!getClass().isInstance(obj)) return false;
        Result otherResult = (Result)obj;
        return either.equals(otherResult.either);
    }

    @Override
    public String toString() {
        return either.fold(error -> "Failure (" + error.getMessage() + ")", t -> "Ok (" + t.toString() + ")");
    }

    private Either<Error, T> either;
}

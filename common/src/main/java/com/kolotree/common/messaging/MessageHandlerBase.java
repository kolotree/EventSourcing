package com.kolotree.common.messaging;

import com.kolotree.common.Nothing;
import com.kolotree.common.Result;

import java.util.LinkedList;
import java.util.List;

public abstract class MessageHandlerBase<T extends Message> implements MessageHandler<T> {

    @Override
    public Result<Nothing> handle(T message) {
        return Result.flatten(handleHierarchically(message)).toNothing();
    }

    private List<Result<Nothing>> handleHierarchically(T message) {
        List<Result<Nothing>> results = new LinkedList<>();
        Class<?> type = message.getClass();
        while (Message.class.isAssignableFrom(type)) {
            if (getMessageHandlers().containsKey(type)) {
                results.add(getMessageHandlers().get(type).apply(message));
            }
            type = type.getSuperclass();
        }
        return results;
    }
}

package com.kolotree.common.messaging;

import com.kolotree.common.Nothing;
import com.kolotree.common.Result;

import java.util.Map;
import java.util.function.Function;

public interface MessageHandler<T extends Message> {

    Result<Nothing> handle(T message);

    Map<Class<? extends T>, Function<T, Result<Nothing>>> getMessageHandlers();
}

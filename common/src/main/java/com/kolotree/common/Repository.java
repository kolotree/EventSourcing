package com.kolotree.common;

import com.kolotree.common.messaging.AggregateRootCreated;
import com.kolotree.common.messaging.EventBus;

import java.util.UUID;
import java.util.function.Function;

public interface Repository<T extends AggregateRoot, K extends AggregateRootCreated> {

    EventBus getEventBus();

    Class<T> getAggregateRootType();

    Class<K> getAggregateRootCreatedType();

    Result<T> createFrom(K aggregateRootCreated);

    Result<T> addNew(T aggregateRoot);

    Result<T> borrow(UUID aggregateRootId, Function<T, Result<T>> transformer);

}

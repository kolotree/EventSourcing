package com.kolotree.repository;

import com.kolotree.common.AggregateRoot;
import com.kolotree.common.Repository;
import com.kolotree.common.Result;
import com.kolotree.common.error.AggregateRootAlreadyExists;
import com.kolotree.common.error.AggregateRootNotFoundInRepositoryError;
import com.kolotree.common.messaging.AggregateRootCreated;
import com.kolotree.common.messaging.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class InMemoryRepository<T extends AggregateRoot, K extends AggregateRootCreated> implements Repository<T, K> {

    private final EventBus eventBus;
    private final Map<UUID, T> cache = new HashMap<>();

    public InMemoryRepository(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Result<T> addNew(T aggregateRoot) {
        if (cache.containsKey(aggregateRoot.getId()))
            return Result.fail(new AggregateRootAlreadyExists(aggregateRoot.getId()));
        cache.put(aggregateRoot.getId(), aggregateRoot);
        return Result.ok(dispatchAllEvents(aggregateRoot));
    }

    private T dispatchAllEvents(T aggregateRoot) {
        eventBus.dispatchAll(aggregateRoot.getDomainEvents());
        aggregateRoot.clearDomainEvents();
        return aggregateRoot;
    }

    @Override
    public Result<T> borrow(UUID aggregateRootId, Function<T, Result<T>> transformer) {
        if (!cache.containsKey(aggregateRootId))
            return Result.fail(new AggregateRootNotFoundInRepositoryError(aggregateRootId));
        return transformer.apply(cache.get(aggregateRootId)).map(this::dispatchAllEvents);
    }
}

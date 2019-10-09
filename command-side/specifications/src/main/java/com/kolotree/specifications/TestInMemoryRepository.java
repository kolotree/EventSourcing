package com.kolotree.specifications;

import com.kolotree.common.AggregateRoot;
import com.kolotree.common.Repository;
import com.kolotree.common.Result;
import com.kolotree.common.error.AggregateRootAlreadyExists;
import com.kolotree.common.error.AggregateRootNotFoundInRepositoryError;
import com.kolotree.common.messaging.AggregateRootCreated;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class TestInMemoryRepository<T extends AggregateRoot, K extends AggregateRootCreated> implements Repository<T, K> {

    private MessageAggregatorEventBus eventBus = new MessageAggregatorEventBus();
    protected Map<UUID, T> aggregateRoots = new HashMap<>();

    @Override
    public final MessageAggregatorEventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Result<T> addNew(T aggregateRoot) {
        if (aggregateRoots.containsKey(aggregateRoot.getId()))
            return Result.fail(new AggregateRootAlreadyExists(aggregateRoot.getId()));
        aggregateRoots.put(aggregateRoot.getId(), aggregateRoot);
        return Result.ok(dispatchAllEvents(aggregateRoot));
    }

    private T dispatchAllEvents(T aggregateRoot) {
        eventBus.dispatchAll(aggregateRoot.getDomainEvents());
        aggregateRoot.clearDomainEvents();
        return aggregateRoot;
    }

    @Override
    public Result<T> borrow(UUID aggregateRootId, Function<T, Result<T>> transformer) {
        if (!aggregateRoots.containsKey(aggregateRootId))
            return Result.fail(new AggregateRootNotFoundInRepositoryError(aggregateRootId));
        return transformer.apply(aggregateRoots.get(aggregateRootId)).map(this::dispatchAllEvents);
    }
}

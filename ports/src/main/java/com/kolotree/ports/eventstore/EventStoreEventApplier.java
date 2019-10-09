package com.kolotree.ports.eventstore;

import com.kolotree.common.AggregateRoot;
import com.kolotree.common.Nothing;
import com.kolotree.common.Repository;
import com.kolotree.common.Result;
import com.kolotree.common.messaging.AggregateRootCreated;
import com.kolotree.common.messaging.DomainEvent;

import java.util.List;

import static com.kolotree.common.Nothing.*;

public class EventStoreEventApplier {

    public static <T extends AggregateRoot, K extends AggregateRootCreated> Nothing applyAll(List<DomainEvent> events, Repository<T, K> repository) {
        events.stream()
                .filter(event -> event.getAggregateRootType().equals(repository.getAggregateRootType().getSimpleName()))
                .forEach(event -> handle(event, repository));
        return NOTHING;
    }

    @SuppressWarnings("unchecked")
    private static <K extends AggregateRootCreated, T extends AggregateRoot> void handle(DomainEvent event, Repository<T, K> repository) {
        if (event instanceof AggregateRootCreated) {
            Result<T> aggregateRoot = repository.createFrom((K) event);
            if (aggregateRoot.isFailure()) throw new EventStore.Exception("Could not create instance of " +
                    event.getAggregateRootType() + ". Error is: " + aggregateRoot.getError());
        } else {
            repository.borrow(event.getAggregateRootId(), t -> tryToApply(t, event));
        }
    }

    private static <T extends AggregateRoot> Result<T> tryToApply(T aggregateRoot, DomainEvent event) {
        if (aggregateRoot.canApply(event)) {
            checkAggregateRootVersionAgainst(aggregateRoot, event).andThen(() -> aggregateRoot.apply(event));
        }
        return Result.ok(aggregateRoot);
    }

    private static <T extends AggregateRoot> Nothing checkAggregateRootVersionAgainst(T aggregateRoot, DomainEvent event) {
        if (event.getAggregateRootVersion() != aggregateRoot.getVersion() + 1)
            throw new EventStore.Exception("Expected to apply " + event + " but version " +
                    aggregateRoot.getVersion() + " of aggregate root found");
        return NOTHING;
    }
}

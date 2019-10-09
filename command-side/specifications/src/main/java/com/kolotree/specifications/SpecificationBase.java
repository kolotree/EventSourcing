package com.kolotree.specifications;

import com.kolotree.common.AggregateRoot;
import com.kolotree.common.Nothing;
import com.kolotree.common.Repository;
import com.kolotree.common.Result;
import com.kolotree.common.messaging.AggregateRootCreated;
import com.kolotree.common.messaging.DomainEvent;

import java.util.List;

public abstract class SpecificationBase<T extends AggregateRoot, K extends AggregateRootCreated> implements Specification<T> {

    private Result<Nothing> result;
    private final T aggregateRoot;
    private Repository<T, K> repository;

    @SuppressWarnings("unchecked")
    public SpecificationBase(Repository<T, K> repository) {
        this.repository = repository;
        if (given().isEmpty() || !(repository.getAggregateRootCreatedType().isInstance(given().get(0))))
            throw new IllegalArgumentException("First given event should be of type " +
                    repository.getAggregateRootCreatedType().getSimpleName());
        aggregateRoot = repository.createFrom((K)given().get(0)).get();
        given().stream().skip(1).forEach(aggregateRoot::apply);
        when().handle(commandToExecute()).onBoth(this::setResult);
    }

    private void setResult(Result<Nothing> result) {
        this.result = result;
    }

    @Override
    public final List<DomainEvent> producedEvents() {
        return ((MessageAggregatorEventBus) repository.getEventBus()).getProducedEvents();
    }

    @Override
    public final Result<Nothing> result() {
        return result;
    }

    protected Repository<T, K> getRepository() {
        return repository;
    }
}

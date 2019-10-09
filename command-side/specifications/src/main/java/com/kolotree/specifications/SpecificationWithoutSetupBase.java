package com.kolotree.specifications;

import com.kolotree.common.AggregateRoot;
import com.kolotree.common.Nothing;
import com.kolotree.common.Repository;
import com.kolotree.common.Result;
import com.kolotree.common.messaging.AggregateRootCreated;
import com.kolotree.common.messaging.DomainEvent;

import java.util.List;

public abstract class SpecificationWithoutSetupBase<T extends AggregateRoot, K extends AggregateRootCreated> implements SpecificationWithoutSetup<T> {

    private Result<Nothing> result;
    private Repository<T, K> repository;

    public SpecificationWithoutSetupBase(Repository<T, K> repository) {
        this.repository = repository;
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


package com.kolotree.common.messaging;

import java.util.UUID;

public abstract class AggregateRootCreated extends DomainEvent {

    protected AggregateRootCreated(UUID aggregateRootId, String aggregateRootType) {
        super(aggregateRootId, aggregateRootType);
    }
}

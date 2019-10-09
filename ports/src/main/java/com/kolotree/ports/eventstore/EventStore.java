package com.kolotree.ports.eventstore;

import com.kolotree.common.messaging.DomainEvent;

import java.util.List;

public interface EventStore {

    DomainEvent append(DomainEvent event);

    List<DomainEvent> loadAll();

    class Exception extends RuntimeException {

        public Exception(String message) {
            super(message);
        }
    }
}

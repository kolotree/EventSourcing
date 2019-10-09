package com.kolotree.adapters.eventstore;

import com.kolotree.common.messaging.DomainEvent;
import com.kolotree.ports.eventstore.EventStore;

import java.util.LinkedList;
import java.util.List;

public class InMemoryEventStore implements EventStore {

    private final List<DomainEvent> events = new LinkedList<>();

    @Override
    public DomainEvent append(DomainEvent event) {
        events.add(event);
        return event;
    }

    @Override
    public List<DomainEvent> loadAll() {
        return events;
    }
}

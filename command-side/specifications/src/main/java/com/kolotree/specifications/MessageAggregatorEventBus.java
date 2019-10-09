package com.kolotree.specifications;

import com.kolotree.common.messaging.DomainEvent;
import com.kolotree.common.messaging.EventBus;
import com.kolotree.common.messaging.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageAggregatorEventBus implements EventBus {

    private List<DomainEvent> producedEvents = new LinkedList<>();

    @Override
    public List<Message> dispatchAll(List<DomainEvent> events) {
        return events.stream().map(this::dispatch).collect(Collectors.toList());
    }

    @Override
    public Message dispatch(DomainEvent event) {
        producedEvents.add(event);
        return event;
    }

    List<DomainEvent> getProducedEvents() {
        return producedEvents;
    }
}

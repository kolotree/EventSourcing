package com.kolotree.adapters.eventstore;

import com.kolotree.common.messaging.DomainEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryEventStoreTest {

    @Test
    public void test_1() {
        InMemoryEventStore eventStore = new InMemoryEventStore();
        Event event1 = (Event) Assertions.assertDoesNotThrow(() -> eventStore.append(new Event(UUID.randomUUID(), "test")));
        Event event2 = (Event) Assertions.assertDoesNotThrow(() -> eventStore.append(new Event(UUID.randomUUID(), "test2")));
        List<DomainEvent> loadedEvents = Assertions.assertDoesNotThrow(eventStore::loadAll);
        Assertions.assertTrue(loadedEvents.stream().allMatch(event -> event instanceof Event));
        Assertions.assertIterableEquals(
                Arrays.asList(event1.getAggregateRootType(), event2.getAggregateRootType()),
                loadedEvents.stream().map(DomainEvent::getAggregateRootType).collect(Collectors.toList()));
        Assertions.assertIterableEquals(
                Arrays.asList(event1.getAggregateRootId(), event2.getAggregateRootId()),
                loadedEvents.stream().map(DomainEvent::getAggregateRootId).collect(Collectors.toList()));
    }

    static class Event extends DomainEvent {

        public Event(UUID aggregateRootId, String aggregateRootType) {
            super(aggregateRootId, aggregateRootType);
        }
    }
}

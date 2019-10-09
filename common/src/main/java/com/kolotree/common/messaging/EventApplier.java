package com.kolotree.common.messaging;

import io.vavr.control.Option;

import java.util.Map;
import java.util.function.Function;

public interface EventApplier {

    DomainEvent apply(DomainEvent event);

    boolean canApply(DomainEvent event);

    class DefaultEventApplier<T> implements EventApplier {

        private final Map<Class<? extends DomainEvent>, Function<DomainEvent, T>> eventAppliers;

        public DefaultEventApplier(Map<Class<? extends DomainEvent>, Function<DomainEvent, T>> eventAppliers) {
            this.eventAppliers = eventAppliers;
        }

        @Override
        public DomainEvent apply(DomainEvent event) {
            applyEventIfApplierExists(event);
            return event;
        }

        private void applyEventIfApplierExists(DomainEvent event) {
            Option.of(eventAppliers.get(event.getClass())).peek(applier -> applier.apply(event));
        }

        @Override
        public boolean canApply(DomainEvent event) {
            return eventAppliers.containsKey(event.getClass());
        }
    }
}

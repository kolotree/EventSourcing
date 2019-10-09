package com.kolotree.common;

import com.kolotree.common.messaging.DomainEvent;
import com.kolotree.common.messaging.EventApplier;

import java.util.*;
import java.util.function.Function;

import static com.kolotree.common.util.CurrentDateAndTimeInUTCProvider.getCurrentDateAndTimeInUTC;

public abstract class AggregateRoot implements EventApplier {

    private final UUID id;
    private long version;
    private final List<DomainEvent> domainEvents;
    private final DefaultEventApplier<AggregateRoot> defaultEventApplier;

    public AggregateRoot(UUID id) {
        this.id = id;
        domainEvents = new LinkedList<>();
        defaultEventApplier = new DefaultEventApplier<>(initializeEventAppliers());
    }

    protected abstract Map<Class<? extends DomainEvent>, Function<DomainEvent, AggregateRoot>> initializeEventAppliers();

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public Nothing clearDomainEvents() {
        domainEvents.clear();
        return Nothing.NOTHING;
    }

    protected void applyChange(DomainEvent event) {
        applyChangeIfApplierExists(event, true);
    }

    @Override
    public boolean canApply(DomainEvent event) {
        return defaultEventApplier.canApply(event);
    }

    @Override
    public DomainEvent apply(DomainEvent event) {
        applyChangeIfApplierExists(event, false);
        return event;
    }

    private void applyChangeIfApplierExists(DomainEvent event, boolean isNew) {
        if (defaultEventApplier.canApply(event)) {
            defaultEventApplier.apply(event);
            applyEvent(isNew, event);
        }
    }

    private void applyEvent(boolean isNew, DomainEvent event) {
        incrementVersion();

        if (!isNew) return;

        event.setAggregateRootVersion(version);
        event.setTimestamp(getCurrentDateAndTimeInUTC());
        domainEvents.add(event);
    }

    private void incrementVersion() {
        version++;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!getClass().isInstance(obj)) return false;
        return id.equals(((AggregateRoot) obj).getId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + id;
    }
}

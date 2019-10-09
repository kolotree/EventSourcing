package com.kolotree.common.view;

import com.kolotree.common.Nothing;
import com.kolotree.common.messaging.DomainEvent;
import com.kolotree.common.messaging.EventApplier;

import java.util.Map;
import java.util.function.Function;

public abstract class View implements EventApplier {

    private final DefaultEventApplier<Nothing> defaultEventApplier;

    protected View() {
        defaultEventApplier = new DefaultEventApplier<>(initializeEventAppliers());
    }

    protected abstract Map<Class<? extends DomainEvent>, Function<DomainEvent, Nothing>> initializeEventAppliers();

    @Override
    public boolean canApply(DomainEvent event) {
        return defaultEventApplier.canApply(event);
    }

    @Override
    public DomainEvent apply(DomainEvent event) {
        return defaultEventApplier.apply(event);
    }
}

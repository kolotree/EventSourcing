package com.kolotree.specifications;

import com.kolotree.common.messaging.DomainEvent;
import com.kolotree.common.view.View;

import java.util.List;

public abstract class ViewSpecification<T extends View> {

    protected final T view;

    protected ViewSpecification() {
        view = buildView();
        when().stream().filter(view::canApply).forEach(view::apply);
    }

    protected abstract T buildView();

    protected abstract List<DomainEvent> when();
}

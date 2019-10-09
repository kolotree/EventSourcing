package com.kolotree.specifications;

import com.kolotree.common.messaging.DomainEvent;
import com.kolotree.common.view.GroupView;
import com.kolotree.common.view.View;

import java.util.List;

public abstract class GroupViewSpecification<T1 extends View, T2 extends GroupView<T1>> {

    protected final T2 groupView;

    protected GroupViewSpecification() {
        groupView = buildView();
        when().stream().filter(groupView::canApply).forEach(groupView::apply);

    }

    protected abstract T2 buildView();

    protected abstract List<DomainEvent> when();
}

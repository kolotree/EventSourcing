package com.kolotree.common.messaging;

import java.util.List;

public interface EventBus {

    List<Message> dispatchAll(List<DomainEvent> events);

    Message dispatch(DomainEvent event);
}

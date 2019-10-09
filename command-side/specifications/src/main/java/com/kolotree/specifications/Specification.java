package com.kolotree.specifications;

import com.kolotree.common.AggregateRoot;
import com.kolotree.common.Nothing;
import com.kolotree.common.Result;
import com.kolotree.common.messaging.Command;
import com.kolotree.common.messaging.CommandHandler;
import com.kolotree.common.messaging.DomainEvent;

import java.util.List;

public interface Specification<T extends AggregateRoot> {

    List<DomainEvent> producedEvents();

    List<DomainEvent> given();

    Command commandToExecute();

    CommandHandler when();

    Result<Nothing> result();
}

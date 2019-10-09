package com.kolotree.common.error;

import java.util.UUID;

public class AggregateRootNotFoundInRepositoryError extends Error {

    public AggregateRootNotFoundInRepositoryError(UUID aggregateRootId) {
        super("Aggregate with id " + aggregateRootId + " not found in repository");
    }

    public AggregateRootNotFoundInRepositoryError(String message) {
        super(message);
    }
}

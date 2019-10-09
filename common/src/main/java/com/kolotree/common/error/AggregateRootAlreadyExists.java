package com.kolotree.common.error;

import java.util.UUID;

public class AggregateRootAlreadyExists extends Error {

    public AggregateRootAlreadyExists(UUID uuid) {
        super("Aggregate root with id " + uuid + " already exists");
    }
}

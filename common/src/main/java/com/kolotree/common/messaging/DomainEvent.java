package com.kolotree.common.messaging;

import com.kolotree.common.ValueObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class DomainEvent extends ValueObject implements Message {

    private final UUID aggregateRootId;
    private final String aggregateRootType;
    private long aggregateRootVersion;

    private Date timestamp;

    protected DomainEvent(UUID aggregateRootId, String aggregateRootType) {
        this.aggregateRootId = aggregateRootId;
        this.aggregateRootType = aggregateRootType;
    }

    public UUID getAggregateRootId() {
        return aggregateRootId;
    }

    public String getAggregateRootType() {
        return aggregateRootType;
    }

    public long getAggregateRootVersion() {
        return aggregateRootVersion;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setAggregateRootVersion(long aggregateRootVersion) {
        this.aggregateRootVersion = aggregateRootVersion;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + aggregateRootId + " v" + getAggregateRootVersion() + ")";
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(aggregateRootId, aggregateRootType);
    }
}

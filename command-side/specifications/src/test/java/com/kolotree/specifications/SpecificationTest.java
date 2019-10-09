package com.kolotree.specifications;

import com.kolotree.common.AggregateRoot;
import com.kolotree.common.Nothing;
import com.kolotree.common.Result;
import com.kolotree.common.messaging.AggregateRootCreated;
import com.kolotree.common.messaging.Command;
import com.kolotree.common.messaging.CommandHandler;
import com.kolotree.common.messaging.DomainEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

public class SpecificationTest {

    @Test
    public void test_1() {
        TestAggregateRootSpecification.uuid = UUID.randomUUID();
        TestAggregateRootSpecification.initiallyClean = true;
        TestAggregateRootSpecification specification = new TestAggregateRootSpecification();
        Assertions.assertTrue(specification.result().isSuccess());
        Assertions.assertIterableEquals(
                Collections.singletonList(new AggregateRootMadeDirty(TestAggregateRootSpecification.uuid)),
                specification.producedEvents()
        );
    }

    @Test
    public void test_2() {
        TestAggregateRootSpecification.uuid = UUID.randomUUID();
        TestAggregateRootSpecification.initiallyClean = false;
        TestAggregateRootSpecification specification = new TestAggregateRootSpecification();
        Assertions.assertFalse(specification.result().isSuccess());
        Assertions.assertTrue(specification.producedEvents().isEmpty());
    }

    static class TestAggregateRootSpecification extends SpecificationBase<TestAggregateRoot, TestAggregateRootCreated> {

        private static UUID uuid;
        private static boolean initiallyClean;

        public TestAggregateRootSpecification() {
            super(new TestAggregateRootRepository());
        }

        @Override
        protected TestAggregateRootRepository getRepository() {
            return (TestAggregateRootRepository) super.getRepository();
        }

        @Override
        public List<DomainEvent> given() {
            return Collections.singletonList(
                    new TestAggregateRootCreated(uuid, initiallyClean)
            );
        }

        @Override
        public Command commandToExecute() {
            return new MakeAggregateRootDirty(uuid);
        }

        @Override
        public CommandHandler when() {
            return new CommandHandler() {
                @Override
                public Map<Class<? extends Command>, Function<Command, Result<Nothing>>> getMessageHandlers() {
                    Map<Class<? extends Command>, Function<Command, Result<Nothing>>> map = new HashMap<>();
                    map.put(MakeAggregateRootDirty.class, command -> getRepository().borrow(
                            ((MakeAggregateRootDirty)command).getUuid(),
                            TestAggregateRoot::makeDirty
                    ).toNothing());
                    return map;
                }
            };
        }
    }

    static class TestAggregateRootRepository extends TestInMemoryRepository<TestAggregateRoot, TestAggregateRootCreated> {

        @Override
        public Class<TestAggregateRoot> getAggregateRootType() {
            return TestAggregateRoot.class;
        }

        @Override
        public Class<TestAggregateRootCreated> getAggregateRootCreatedType() {
            return TestAggregateRootCreated.class;
        }

        @Override
        public Result<TestAggregateRoot> createFrom(TestAggregateRootCreated aggregateRootCreated) {
            return Result.ok(new TestAggregateRoot(
                    aggregateRootCreated.getAggregateRootId(),
                    aggregateRootCreated.isClean())
            ).flatMap(this::addNew);
        }
    }

    static class TestAggregateRoot extends AggregateRoot {

        private boolean clean;

        public TestAggregateRoot(UUID id, boolean clean) {
            super(id);
            this.clean = clean;
        }

        public Result<TestAggregateRoot> makeDirty() {
            if (!clean) return Result.fail("Already dirty");
            applyChange(new AggregateRootMadeDirty(getId()));
            return Result.ok(this);
        }

        private TestAggregateRoot applyAggregateRootMadeDirty() {
            clean = false;
            return this;
        }

        @Override
        protected Map<Class<? extends DomainEvent>, Function<DomainEvent, AggregateRoot>> initializeEventAppliers() {
            Map<Class<? extends DomainEvent>, Function<DomainEvent, AggregateRoot>> map = new HashMap<>();
            map.put(AggregateRootMadeDirty.class, event -> applyAggregateRootMadeDirty());
            return map;
        }
    }

    static class TestAggregateRootCreated extends AggregateRootCreated {

        private final boolean clean;

        protected TestAggregateRootCreated(UUID aggregateRootId, boolean clean) {
            super(aggregateRootId, TestAggregateRoot.class.getSimpleName());
            this.clean = clean;
        }

        public boolean isClean() {
            return clean;
        }
    }

    static class AggregateRootMadeDirty extends DomainEvent {

        protected AggregateRootMadeDirty(UUID aggregateRootId) {
            super(aggregateRootId, TestAggregateRoot.class.getSimpleName());
        }
    }

    static class MakeAggregateRootDirty extends Command {
        private final UUID uuid;

        MakeAggregateRootDirty(UUID uuid) {
            this.uuid = uuid;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}

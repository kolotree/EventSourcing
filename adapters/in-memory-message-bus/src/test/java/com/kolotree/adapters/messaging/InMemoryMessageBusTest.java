package com.kolotree.adapters.messaging;

import com.kolotree.common.Nothing;
import com.kolotree.common.Result;
import com.kolotree.common.messaging.Command;
import com.kolotree.common.messaging.CommandHandler;
import com.kolotree.common.messaging.DomainEvent;
import com.kolotree.common.messaging.EventHandler;
import com.kolotree.ports.logger.ConsoleLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class InMemoryMessageBusTest {

    @Test
    public void testCommandHandling() {
        InMemoryMessageBus messageBus = new InMemoryMessageBus(new ConsoleLogger());
        TestCommandHandler testCommandHandler = new TestCommandHandler();
        messageBus.register(testCommandHandler);
        messageBus.scheduleOneWayCommand(new TestCommand());
        messageBus.scheduleOneWayCommand(new TestCommand2());
        Assertions.assertEquals(2, testCommandHandler.counter);
    }

    @Test
    public void testEventHandling() {
        InMemoryMessageBus messageBus = new InMemoryMessageBus(new ConsoleLogger());
        TestEventHandler testEventHandler = new TestEventHandler();
        messageBus.register(testEventHandler);
        messageBus.dispatchAll(Arrays.asList(new TestEvent(), new TestEvent2()));
        Assertions.assertEquals(2, testEventHandler.counter);
    }

    static class TestCommandHandler extends CommandHandler {

        private int counter = 0;

        @Override
        public Map<Class<? extends Command>, Function<Command, Result<Nothing>>> getMessageHandlers() {
            Map<Class<? extends Command>, Function<Command, Result<Nothing>>> map = new HashMap<>();
            map.put(TestCommand.class, command -> increaseCounter());
            map.put(Command.class, command -> increaseCounter());
            return map;
        }

        private Result<Nothing> increaseCounter() {
            counter++;
            return Result.ok();
        }
    }

    static class TestCommand extends Command {
    }

    static class TestCommand2 extends Command {
    }

    static class TestEventHandler extends EventHandler {

        private int counter = 0;

        @Override
        public Map<Class<? extends DomainEvent>, Function<DomainEvent, Result<Nothing>>> getMessageHandlers() {
            Map<Class<? extends DomainEvent>, Function<DomainEvent, Result<Nothing>>> map = new HashMap<>();
            map.put(TestEvent.class, event -> increaseCounter());
            map.put(DomainEvent.class, event -> increaseCounter());
            return map;
        }

        private Result<Nothing> increaseCounter() {
            counter++;
            return Result.ok();
        }
    }

    static class TestEvent extends DomainEvent {

        protected TestEvent() {
            super(UUID.randomUUID(), "test");
        }
    }

    static class TestEvent2 extends DomainEvent {

        protected TestEvent2() {
            super(UUID.randomUUID(), "test2");
        }
    }
}

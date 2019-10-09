package com.kolotree.adapters.messaging;

import com.kolotree.common.Nothing;
import com.kolotree.common.Result;
import com.kolotree.common.messaging.*;
import com.kolotree.ports.logger.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kolotree.common.Nothing.NOTHING;
import static com.kolotree.common.Result.flatten;
import static java.util.Collections.emptyList;

public class InMemoryMessageBus implements CommandBus, EventBus {

    private final Map<Class<? extends DomainEvent>, List<EventHandler>> eventHandlers;
    private final Map<Class<? extends Command>, List<CommandHandler>> commandHandlers;

    private final Object lock = new Object();
    private final Logger logger;

    public InMemoryMessageBus(Logger logger) {
        this.logger = logger;
        eventHandlers = new HashMap<>();
        commandHandlers = new HashMap<>();
    }

    public <T extends Message> Nothing register(MessageHandler<T> messageHandler) {
        if (messageHandler instanceof EventHandler) {
            EventHandler handler = (EventHandler)messageHandler;
            handler.getMessageHandlers().keySet().forEach(type ->
                    eventHandlers.computeIfAbsent(type, c -> new LinkedList<>()).add(handler));
        } else if (messageHandler instanceof CommandHandler) {
            CommandHandler handler = (CommandHandler)messageHandler;
            handler.getMessageHandlers().keySet().forEach(type ->
                    commandHandlers.computeIfAbsent(type, c -> new LinkedList<>()).add(handler));
        }
        return NOTHING;
    }

    @Override
    public Result<Nothing> execute(Command command) {
        return dispatchMessageToAllRegisteredHandlers(command);
    }

    @Override
    public Nothing scheduleOneWayCommand(Command command) {
        dispatchMessageToAllRegisteredHandlers(command);
        return NOTHING;
    }

    @Override
    public List<Message> dispatchAll(List<DomainEvent> events) {
        return events.stream().map(this::dispatch).collect(Collectors.toList());
    }

    @Override
    public Message dispatch(DomainEvent event) {
        dispatchMessageToAllRegisteredHandlers(event);
        return event;
    }

    private <T extends Message> Result<Nothing> dispatchMessageToAllRegisteredHandlers(T message) {
        logger.debug("Dispatching " + message + "...");
        synchronized (lock) {
            return flatten(getMessageHandlersFor(message).stream()
                    .map(handler -> handler.handle(message))
                    .collect(Collectors.toList())).map(list -> NOTHING)
                    .onBoth(result -> logger.debug("Result of handling " + message + " is " + result));
        }
    }

    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    private <T extends Message> List<MessageHandler<T>> getMessageHandlersFor(T message) {
        final List<MessageHandler<T>> handlers = new LinkedList<>();
        if (message instanceof DomainEvent) {
            eventHandlers.getOrDefault(message.getClass(), emptyList())
                    .forEach(eventHandler -> handlers.add((MessageHandler<T>) eventHandler));
        } else if (message instanceof Command) {
            commandHandlers.getOrDefault(message.getClass(), emptyList())
                    .forEach(commandHandler -> handlers.add((MessageHandler<T>) commandHandler));
        }
        return handlers;
    }
}

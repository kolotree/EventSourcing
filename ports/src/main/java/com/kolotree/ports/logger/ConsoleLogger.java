package com.kolotree.ports.logger;

import com.kolotree.common.Nothing;

import static com.kolotree.common.Nothing.NOTHING;

public class ConsoleLogger implements Logger {

    @Override
    public Nothing debug(String message) {
        System.out.println("[DEBUG] " + message);
        return NOTHING;
    }

    @Override
    public Nothing info(String message) {
        System.out.println("[INFO] " + message);
        return NOTHING;
    }

    @Override
    public Nothing warning(String message) {
        System.out.println("[WARNING] " + message);
        return NOTHING;
    }

    @Override
    public Nothing error(String message) {
        System.err.println("[ERROR] " + message);
        return NOTHING;
    }

    @Override
    public Nothing error(String message, Throwable e) {
        System.err.println("[ERROR] " + message + System.lineSeparator() + e);
        return NOTHING;
    }

    @Override
    public Nothing fatal(String message) {
        System.err.println("[FATAL] " + message);
        return NOTHING;
    }

    @Override
    public Nothing fatal(String message, Throwable e) {
        System.err.println("[FATAL] " + message + System.lineSeparator() + e);
        return NOTHING;
    }
}

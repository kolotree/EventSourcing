package com.kolotree.ports.logger;

import com.kolotree.common.Nothing;

public interface Logger {

    Nothing debug(String message);

    Nothing info(String message);

    Nothing warning(String message);

    Nothing error(String message);

    Nothing error(String message, Throwable e);

    Nothing fatal(String message);

    Nothing fatal(String message, Throwable e);
}

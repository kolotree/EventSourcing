package com.kolotree.common.messaging;

import com.kolotree.common.Nothing;
import com.kolotree.common.Result;

public interface CommandBus {

    Result<Nothing> execute(Command command);

    Nothing scheduleOneWayCommand(Command command);
}

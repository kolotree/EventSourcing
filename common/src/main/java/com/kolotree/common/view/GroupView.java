package com.kolotree.common.view;

import com.kolotree.common.Id;
import com.kolotree.common.messaging.EventApplier;
import io.vavr.control.Option;

public interface GroupView<T extends View> extends EventApplier {

    Option<T> viewFor(Id id);
}

package io.virtue.registry.support;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * Register service task.
 */
@Getter
@Accessors(fluent = true)
public class RegisterTask implements Runnable {

    private final Consumer<RegisterTask> task;

    private final boolean isUpdate;

    public RegisterTask(Consumer<RegisterTask> task, boolean isUpdate) {
        this.task = task;
        this.isUpdate = isUpdate;
    }

    @Override
    public void run() {
        task.accept(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

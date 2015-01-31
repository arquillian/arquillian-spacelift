package org.arquillian.spacelift.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NoArgConstructorTaskFactory<IN, OUT, TASK extends Task<? super IN, OUT>> implements TaskFactory<IN, OUT, TASK> {

    private final Class<TASK> taskDef;
    private final List<String> aliases;

    public NoArgConstructorTaskFactory(Class<TASK> taskDef, String... aliases) {
        this.taskDef = taskDef;
        this.aliases = Arrays.asList(aliases);
    }

    @Override
    public TASK create() {
        TASK task = SecurityActions.newInstance(taskDef);
        return new TaskFactory.ExecutionServiceInjector().inject(task);
    }

    @Override
    public Collection<String> aliases() {
        return aliases;
    }

}

package com.molean.isletopia.velocity;

import com.molean.isletopia.shared.annotations.Bean;

import java.util.ArrayList;
import java.util.List;

@Bean
public class DisableTasks {
    private final List<Runnable> tasks = new ArrayList<>();


    public void add(Runnable runnable) {
        this.tasks.add(runnable);

    }

    public List<Runnable> getTasks() {
        return tasks;
    }
}

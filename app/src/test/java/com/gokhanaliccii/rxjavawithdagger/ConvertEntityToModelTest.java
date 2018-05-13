package com.gokhanaliccii.rxjavawithdagger;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by gokhan on 13/05/18.
 */

public class ConvertEntityToModelTest {

    @Test
    public void should_ConvertTaskEntitiesToTasksCorrectly() {
        final List<TaskEntity> taskEntities = Arrays.asList(createDummyEntities());

        Flowable.fromIterable(taskEntities).map(new Function<TaskEntity, Task>() {
            @Override
            public Task apply(TaskEntity taskEntity) throws Exception {
                return taskEntity.toTask();
            }
        }).subscribe(new Consumer<Task>() {
            @Override
            public void accept(Task task) throws Exception {
                System.out.println(task.toString());
            }
        });


    }

    @NonNull
    private TaskEntity[] createDummyEntities() {
        return new TaskEntity[]{new TaskEntity(1, "first task", "first"),
                new TaskEntity(1, "second task", "second")};
    }


    private static class TaskEntity {
        private long taskId;
        private String title;
        private String description;

        public TaskEntity(long taskId, String title, String description) {
            this.taskId = taskId;
            this.title = title;
            this.description = description;
        }

        public Task toTask() {
            return Task.createTask(taskId, title, description);
        }

        @Override
        public String toString() {
            return "taskId:" + taskId + ", title:" + title + ", description:" + description;
        }
    }

    private static class Task {
        private long taskId;
        private String title;
        private String description;

        private Task(long taskId, String title, String description) {
            this.taskId = taskId;
            this.title = title;
            this.description = description;
        }

        @Override
        public String toString() {
            return "taskId:" + taskId + ", title:" + title + ", description:" + description;
        }

        public static Task createTask(long taskId, String title, String description) {
            return new Task(taskId, title, description);
        }
    }
}

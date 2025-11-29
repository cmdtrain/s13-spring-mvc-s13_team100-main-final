package com.softserve.itacademy.dto;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.TaskPriority;
import org.springframework.stereotype.Component;

@Component
public class TaskTransformer {

    public TaskDto convertToDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getPriority().toString(),
                task.getTodo().getId(),
                task.getState().getId()
        );
    }

    public Task fillEntityFields(Task task, TaskDto taskDto, ToDo todo, State state) {
        task.setId(taskDto.getId());
        task.setName(taskDto.getName());
        task.setPriority(TaskPriority.valueOf(taskDto.getPriority()));
        task.setTodo(todo);
        task.setState(state);
        return task;
    }
}

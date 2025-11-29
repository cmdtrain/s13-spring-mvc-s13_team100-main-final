package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskTransformer taskTransformer;
    private final StateService stateService;
    private final ToDoService todoService;

    @GetMapping("/create/todos/{todo_id}")
    public String createTask(@PathVariable("todo_id") long todoId, Model model) {
        TaskDto taskDto = new TaskDto();
        taskDto.setTodoId(todoId);
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", TaskPriority.values());
        return "create-task";
    }

    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId,
                         @Validated @ModelAttribute("task") TaskDto taskDto,
                         BindingResult bindingResult,
                         Model model) {
        taskDto.setTodoId(todoId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            return "create-task";
        }
        taskService.create(taskDto);
        return "redirect:/todos/" + todoId + "/read";
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String taskUpdateForm(@PathVariable("task_id") long taskId,
                                 @PathVariable("todo_id") long todoId,
                                 Model model) {
        Task task = taskService.readById(taskId);
        TaskDto taskDto = taskTransformer.convertToDto(task);
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("states", stateService.getAll());
        return "update-task";
    }

    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId,
                         @PathVariable("todo_id") long todoId,
                         @Validated @ModelAttribute("task") TaskDto taskDto,
                         BindingResult bindingResult,
                         Model model) {
        taskDto.setId(taskId);
        taskDto.setTodoId(todoId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }
        Task existing = taskService.readById(taskId);
        Task updated = taskTransformer.fillEntityFields(
                existing,
                taskDto,
                todoService.readById(todoId),
                stateService.readById(taskDto.getStateId())
        );
        taskService.update(updated);
        return "redirect:/todos/" + todoId + "/read";
    }
}

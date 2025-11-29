package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping("/create/users/{owner_id}")
    public String createToDoForm(@PathVariable("owner_id") long ownerId, Model model) {
        User owner = userService.readById(ownerId);
        ToDo todo = new ToDo();
        todo.setOwner(owner);
        model.addAttribute("owner", owner);
        model.addAttribute("todo", todo);
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String createToDo(@PathVariable("owner_id") long ownerId,
                             @Validated @ModelAttribute("todo") ToDo todo,
                             BindingResult bindingResult,
                             Model model) {
        User owner = userService.readById(ownerId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("owner", owner);
            return "create-todo";
        }
        todo.setOwner(owner);
        todo.setCreatedAt(LocalDateTime.now());
        todoService.create(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId,
                         @PathVariable("owner_id") long ownerId,
                         Model model) {
        ToDo todo = todoService.readById(todoId);
        User owner = userService.readById(ownerId);
        model.addAttribute("owner", owner);
        model.addAttribute("todo", todo);
        return "update-todo";
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId,
                         @PathVariable("owner_id") long ownerId,
                         @RequestParam("title") String title) {
        ToDo todo = todoService.readById(todoId);
        todo.setTitle(title);
        todoService.update(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") long todoId,
                         @PathVariable("owner_id") long ownerId) {
        todoService.delete(todoId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") long userId, Model model) {
        User user = userService.readById(userId);
        List<ToDo> todos = todoService.getByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("todos", todos);
        return "todos-user";
    }

    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable("id") long todoId,
                                  @RequestParam("collaboratorId") long collaboratorId) {
        ToDo todo = todoService.readById(todoId);
        User collaborator = userService.readById(collaboratorId);
        if (!todo.getCollaborators().contains(collaborator) && collaborator.getId() != todo.getOwner().getId()) {
            todo.getCollaborators().add(collaborator);
            todoService.update(todo);
        }
        return "redirect:/todos/" + todoId + "/read";
    }

    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable("id") long todoId,
                                     @RequestParam("collaboratorId") long collaboratorId) {
        ToDo todo = todoService.readById(todoId);
        todo.getCollaborators().removeIf(u -> u.getId() == collaboratorId);
        todoService.update(todo);
        return "redirect:/todos/" + todoId + "/read";
    }

    @GetMapping("/{todo_id}/read")
    public String read(@PathVariable("todo_id") long todoId, Model model) {
        ToDo todo = todoService.readById(todoId);
        List<Task> tasks = taskService.getByTodoId(todoId);
        List<User> collaborators = todo.getCollaborators();
        List<User> users = userService.getAll().stream()
                .filter(u -> u.getId() != todo.getOwner().getId())
                .collect(Collectors.toList());
        model.addAttribute("todo", todo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("collaborators", collaborators);
        model.addAttribute("users", users);
        return "todo-tasks";
    }
}

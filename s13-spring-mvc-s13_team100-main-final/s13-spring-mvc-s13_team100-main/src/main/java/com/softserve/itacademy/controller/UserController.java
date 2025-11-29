package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
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

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new CreateUserDto());
        }
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") CreateUserDto createUserDto,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "create-user";
        }
        User user = new User();
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setPassword("{noop}" + createUserDto.getPassword());
        user.setRole(UserRole.USER);
        User saved = userService.create(user);
        session.setAttribute("username", saved.getFirstName());
        session.setAttribute("user_id", saved.getId());
        return "redirect:/todos/all/users/" + saved.getId();
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable("id") long id, Model model) {
        UserDto user = userService.findByIdThrowing(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") long id, Model model) {
        UserDto userDto = userService.findByIdThrowing(id);
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userDto.getId());
        updateUserDto.setFirstName(userDto.getFirstName());
        updateUserDto.setLastName(userDto.getLastName());
        updateUserDto.setEmail(userDto.getEmail());
        updateUserDto.setRole(userDto.getRole());
        model.addAttribute("user", updateUserDto);
        model.addAttribute("roles", UserRole.values());
        return "update-user";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") long id,
                         @Validated @ModelAttribute("user") UpdateUserDto updateUserDto,
                         BindingResult bindingResult,
                         Model model) {
        updateUserDto.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }
        userService.update(updateUserDto);
        return "redirect:/users/all";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        List<UserDto> users = userService.findAll();
        model.addAttribute("users", users);
        return "users-list";
    }
}

package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute("user_id") != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        Optional<User> optionalUser = userService.findByUsername(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String storedPassword = user.getPassword();
            if (storedPassword.equals(password) || storedPassword.equals("{noop}" + password)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", user.getFirstName());
                session.setAttribute("user_id", user.getId());
                return "redirect:/home";
            }
        }
        model.addAttribute("loginError", true);
        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

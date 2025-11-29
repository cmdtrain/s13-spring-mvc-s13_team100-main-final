package com.softserve.itacademy.controller.advice;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    @ModelAttribute
    public void addCurrentUser(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username);
        }
        Long userId = (Long) session.getAttribute("user_id");
        if (userId != null) {
            model.addAttribute("user_id", userId);
        }
    }
}

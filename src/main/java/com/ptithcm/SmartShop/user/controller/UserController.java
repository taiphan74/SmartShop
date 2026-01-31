package com.ptithcm.SmartShop.user.controller;

import com.ptithcm.SmartShop.user.model.Gender;
import com.ptithcm.SmartShop.user.model.User;
import com.ptithcm.SmartShop.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("mode", "create");
        return "users/form";
    }

    @PostMapping
    public String create(@ModelAttribute User user) {
        userService.create(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "users/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("user", userService.getById(id));
        model.addAttribute("genders", Gender.values());
        model.addAttribute("mode", "edit");
        return "users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @ModelAttribute User user) {
        userService.update(id, user);
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id) {
        userService.delete(id);
        return "redirect:/users";
    }
}

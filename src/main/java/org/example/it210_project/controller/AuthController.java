package org.example.it210_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.it210_project.model.User;
import org.example.it210_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    @Autowired private UserService userService;

    @GetMapping("/login")
    public String loginPage() { return "auth/login"; }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {
        return userService.login(username, password)
                .map(user -> {
                    session.setAttribute("user", user);
                    if (user.getRole() == User.Role.ADMIN) return "redirect:/admin/movies";
                    if (user.getRole() == User.Role.STAFF) return "redirect:/staff/counter";
                    return "redirect:/user/home";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Sai tài khoản hoặc mật khẩu!");
                    return "auth/login";
                });
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User()); // QUAN TRỌNG: Phải có dòng này
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute User user, HttpSession session) {
        // Ép kiểu role là CUSTOMER ngay tại đây trước khi lưu
        user.setRole(User.Role.CUSTOMER);

        // Lưu vào database qua service
        User savedUser = userService.register(user);

        // Lưu vào session để các trang sau (như home, profile) có dữ liệu hiển thị
        session.setAttribute("user", savedUser);

        return "redirect:/user/home";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }
}
package org.example.it210_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.it210_project.model.User;
import org.example.it210_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        boolean hasError = false;

        // 1. Kiểm tra trống Username
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("errUser", "Vui lòng nhập tên đăng nhập");
            hasError = true;
        }

        // 2. Kiểm tra trống Password
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("errPass", "Vui lòng nhập mật khẩu");
            hasError = true;
        }

        if (hasError) return "auth/login";

        // 3. Kiểm tra thông tin đăng nhập trong Database
        Optional<User> userOpt = userService.login(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);

            // Điều hướng theo Role
            if (user.getRole() == User.Role.ADMIN) return "redirect:/admin/movies";
            if (user.getRole() == User.Role.STAFF) return "redirect:/staff/dashboard";
            return "redirect:/user/home";
        } else {
            // Nếu sai tài khoản hoặc mật khẩu
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User()); // QUAN TRỌNG: Phải có dòng này
        return "auth/register";
    }


    @PostMapping("/register")
    public String handleRegister(@ModelAttribute User user, HttpSession session, Model model) {
        boolean hasError = false;

        // 1. Check Username
        if (user.getUsername() == null || user.getUsername().length() < 5) {
            model.addAttribute("errUser", "Tên đăng nhập phải ít nhất 5 ký tự");
            hasError = true;
        }

        // 2. Check Email
        if (user.getEmail() == null || !user.getEmail().toLowerCase().endsWith("@gmail.com")) {
            model.addAttribute("errEmail", "Email phải có đuôi @gmail.com");
            hasError = true;
        }

        // 3. Check Password
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            model.addAttribute("errPass", "Mật khẩu phải từ 3 ký tự trở lên");
            hasError = true;
        }

        // 4. Check FullName
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            model.addAttribute("errName", "Vui lòng nhập họ và tên");
            hasError = true;
        }

        // Nếu có bất kỳ lỗi nào thì quay lại trang đăng ký
        if (hasError) {
            return "auth/register";
        }

        // Nếu mọi thứ ổn thì lưu vào DB
        user.setRole(User.Role.CUSTOMER);
        User savedUser = userService.register(user);
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Xóa toàn bộ dữ liệu trong session
        session.invalidate();
        return "redirect:/login";
    }




}
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
        Optional<User> userOpt = userService.login(username, password);
        if (userOpt.isPresent()) {
            session.setAttribute("user", userOpt.get());
            if (userOpt.get().getRole() == User.Role.ADMIN) return "redirect:/admin/movies";
            return "redirect:/user/home";
        }
        model.addAttribute("err", "Sai tài khoản hoặc mật khẩu");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        // PHẢI CÓ DÒNG NÀY: Khởi tạo User rỗng để form trong HTML có cái mà "bám" vào
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute User user,
                                 @RequestParam String phoneNumber,
                                 HttpSession session,
                                 Model model) {
        try {
            // 1. Kiểm tra số điện thoại (Thịnh đã có logic này, mình làm nó chặt chẽ hơn tí)
            if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
                model.addAttribute("errPhone", "Số điện thoại phải đủ 10 chữ số");
                return "auth/register";
            }

            // 2. Lưu user (Nên check xem tên đăng nhập có bị trùng không trong Service)
            User savedUser = userService.register(user, phoneNumber);

            // 3. Đăng nhập luôn sau khi đăng ký thành công
            session.setAttribute("user", savedUser);
            return "redirect:/user/home";

        } catch (Exception e) {
            // Nếu trùng username hoặc lỗi DB, nó sẽ nhảy vào đây
            model.addAttribute("err", "Tên đăng nhập đã tồn tại hoặc lỗi hệ thống!");
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
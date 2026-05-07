package org.example.it210_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.it210_project.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @GetMapping("/counter")
    public String staffPage(HttpSession session) {
        // Kiểm tra bảo mật thủ công: Nếu không phải staff thì đá ra ngoài
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.STAFF) {
            return "redirect:/login";
        }
        return "staff/counter"; // Trả về file templates/staff/counter.html
    }
}
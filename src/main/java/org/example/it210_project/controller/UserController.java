package org.example.it210_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.it210_project.model.User;
import org.example.it210_project.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    private MovieService movieService; // Phải có dòng này để lấy danh sách phim

    @GetMapping("/user/home")
    public String userHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("movies", movieService.getAllMovies());
        return "user/home";
    }
}
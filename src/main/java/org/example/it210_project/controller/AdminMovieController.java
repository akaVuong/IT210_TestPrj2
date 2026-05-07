package org.example.it210_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.it210_project.model.Movie;
import org.example.it210_project.model.User;
import org.example.it210_project.repository.GenreRepository;
import org.example.it210_project.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/movies")
public class AdminMovieController {
    @Autowired private MovieService movieService;
    @Autowired private GenreRepository genreRepository;

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("movies", movieService.getAllMovies());
        return "admin/movie-list";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movie-add";
    }

    @PostMapping("/add")
    public String handleAdd(@ModelAttribute Movie movie, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        movieService.saveMovie(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movie-edit";
    }

    @PostMapping("/edit")
    public String handleEdit(@ModelAttribute Movie movie, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        movieService.saveMovie(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }
}
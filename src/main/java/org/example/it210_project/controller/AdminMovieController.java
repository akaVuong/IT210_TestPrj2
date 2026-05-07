package org.example.it210_project.controller;

import org.example.it210_project.model.Movie;
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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "admin/movie-list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movie-add";
    }

    @PostMapping("/add")
    public String handleAdd(@ModelAttribute Movie movie) {
        movieService.saveMovie(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movie-edit";
    }

    @PostMapping("/edit")
    public String handleEdit(@ModelAttribute Movie movie) {
        // movie lúc này đã có ID từ thẻ hidden trong HTML nên JPA sẽ tự Update
        movieService.saveMovie(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }
}
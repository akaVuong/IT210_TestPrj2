package org.example.it210_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.it210_project.model.User;
import org.example.it210_project.repository.ShowtimeRepository;
import org.example.it210_project.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private TicketRepository ticketRepository;

    @GetMapping("/user/home")
    public String userHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Hiện suất chiếu đang ACTIVE thay vì list phim tĩnh
        model.addAttribute("showtimes", showtimeRepository.findAll());
        return "user/home";
    }

    @GetMapping("/user/tickets")
    public String myTickets(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Xem lịch sử đặt vé (CORE-07)
        model.addAttribute("tickets", ticketRepository.findByUserOrderByBookingTimeDesc(user));
        return "user/my-tickets";
    }


}
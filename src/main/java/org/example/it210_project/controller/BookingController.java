package org.example.it210_project.controller;

import org.example.it210_project.model.*;
import org.example.it210_project.repository.*;
import org.example.it210_project.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user") // Đưa về gốc /user để quản lý chung tickets và booking
public class BookingController {

    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private TicketDetailRepository ticketDetailRepository;
    @Autowired private TicketRepository ticketRepository; // Cần dùng để lấy lịch sử vé
    @Autowired private BookingService bookingService;

    // 1. Hiển thị sơ đồ ghế (4x4)
    @GetMapping("/booking/{showtimeId}")
    public String showSeatPlan(@PathVariable Long showtimeId, Model model) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        // Lấy danh sách ID các ghế đã được đặt để tô màu đỏ
        List<Long> bookedSeatIds = ticketDetailRepository.findBookedSeatIdsByShowtime(showtimeId);

        model.addAttribute("showtime", showtime);
        model.addAttribute("bookedSeatIds", bookedSeatIds);

        return "user/booking"; // File booking.html nằm trong templates/user/
    }

    // 2. Xử lý xác nhận đặt vé (CORE-06)
    @PostMapping("/booking/confirm")
    public String confirmBooking(@RequestParam Long showtimeId,
                                 @RequestParam List<Long> seatIds,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";

        try {
            Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow();
            // Gọi Service xử lý Transaction an toàn
            bookingService.createBooking(currentUser, showtime, seatIds);

            ra.addFlashAttribute("success", "Đặt vé thành công!");
            return "redirect:/user/my-tickets"; // Chuyển hướng về trang danh sách vé
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ghế đã có người đặt hoặc lỗi hệ thống!");
            return "redirect:/user/booking/" + showtimeId;
        }
    }

    // 3. Hiển thị danh sách vé đã đặt (Lịch sử)
    @GetMapping("/my-tickets")
    public String showMyTickets(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";

        // Lấy danh sách vé của User
        List<Ticket> tickets = ticketRepository.findByUserOrderByBookingTimeDesc(currentUser);

        // MẸO QUAN TRỌNG: Ép Hibernate tải dữ liệu TicketDetails trước khi sang trang HTML
        for (Ticket t : tickets) {
            t.getTicketDetails().size();
        }

        model.addAttribute("tickets", tickets);
        return "user/my-tickets";
    }
}
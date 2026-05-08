package org.example.it210_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.it210_project.model.Movie;
import org.example.it210_project.model.Showtime;
import org.example.it210_project.model.User;
import org.example.it210_project.repository.GenreRepository;
import org.example.it210_project.repository.RoomRepository;
import org.example.it210_project.repository.ShowtimeRepository;
import org.example.it210_project.repository.TicketRepository;
import org.example.it210_project.service.MovieService;
import org.example.it210_project.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminMovieController {

    @Autowired private MovieService movieService;
    @Autowired private GenreRepository genreRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private ShowtimeService showtimeService;
    @Autowired private TicketRepository ticketRepository;

    // Hàm kiểm tra quyền Admin nhanh
    private User getAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getRole() == User.Role.ADMIN) return user;
        return null;
    }

    // --- 1. DASHBOARD THỐNG KÊ (Sửa lỗi báo cáo doanh thu) ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";

        // Tính doanh thu thực tế từ những vé có trạng thái BOOKED
        double totalRevenue = ticketRepository.findAll().stream()
                .filter(t -> "BOOKED".equals(t.getStatus()))
                .mapToDouble(t -> t.getTotalAmount() != null ? t.getTotalAmount() : 0.0)
                .sum();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalTickets", ticketRepository.count());
        model.addAttribute("movies", movieService.getAllMovies());

        return "admin/dashboard";
    }

    // --- 2. QUẢN LÝ DANH SÁCH PHIM ---
    @GetMapping("/movies")
    public String listMovies(Model model, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";

        // 1. Lấy danh sách phim
        model.addAttribute("movies", movieService.getAllMovies());

        // 2. PHẢI CÓ DÒNG NÀY: Lấy toàn bộ suất chiếu để hiện ở bảng dưới
        // (Đảm bảo bạn đã @Autowired ShowtimeRepository ở đầu class)
        model.addAttribute("showtimes", showtimeRepository.findAll());

        return "admin/movie-list";
    }

    @GetMapping("/movies/add")
    public String addForm(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movie-add";
    }

    @PostMapping("/movies/add")
    public String handleAdd(@ModelAttribute Movie movie, HttpSession session, RedirectAttributes ra) {
        if (getAdmin(session) == null) return "redirect:/login";

        try {
            // Đảm bảo Genre được gán đúng thực thể từ DB
            if (movie.getGenre() != null && movie.getGenre().getId() != null) {
                movie.setGenre(genreRepository.findById(movie.getGenre().getId()).orElse(null));
            }
            movieService.saveMovie(movie);
            ra.addFlashAttribute("success", "Đã thêm phim mới thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/movies/add";
        }
        return "redirect:/admin/movies";
    }

    // --- 3. THIẾT LẬP SUẤT CHIẾU (Fix lỗi logic và thời gian) ---
    @GetMapping("/movies/showtimes/add")
    public String addShowtimeForm(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("showtime", new Showtime());
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("rooms", roomRepository.findAll());
        return "admin/showtime-add";
    }

    @PostMapping("/movies/showtimes/add")
    public String handleAddShowtime(@ModelAttribute("showtime") Showtime showtime, RedirectAttributes ra, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";

        try {
            // 1. Kiểm tra dữ liệu đầu vào
            if (showtime.getMovie() == null || showtime.getMovie().getId() == null) {
                ra.addFlashAttribute("error", "Vui lòng chọn phim!");
                return "redirect:/admin/movies/showtimes/add";
            }

            // 2. Lấy phim từ DB để có Duration (để tính endTime)
            Movie movie = movieService.getMovieById(showtime.getMovie().getId());

            // 3. Tính toán thời gian
            LocalDateTime start = showtime.getStartTime();
            LocalDateTime end = start.plusMinutes(movie.getDuration());

            // 4. Kiểm tra xung đột (CORE-05)
            if (showtimeService.hasConflict(showtime.getRoom().getId(), start, end)) {
                ra.addFlashAttribute("error", "Phòng này đã có lịch chiếu hoặc chưa dọn xong (cần 15p dọn dẹp)!");
                return "redirect:/admin/movies/showtimes/add";
            }

            // 5. Gán dữ liệu và lưu
            showtime.setMovie(movie); // Gán object movie đầy đủ thay vì chỉ có ID
            showtime.setEndTime(end);
            showtime.setStatus("ACTIVE");
            showtimeRepository.save(showtime);

            ra.addFlashAttribute("success", "Tạo suất chiếu thành công!");
        } catch (Exception e) {
            // Nếu có lỗi, in ra Console để mình xem và báo lỗi ra màn hình
            e.printStackTrace();
            ra.addFlashAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/movies/showtimes/add";
        }
        return "redirect:/admin/movies";
    }

    // Thêm hàm xóa phim nếu cần
    @GetMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable Long id, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }

    // 1. Mở trang sửa phim
    @GetMapping("/movies/edit/{id}")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";

        Movie movie = movieService.getMovieById(id);
        if (movie == null) return "redirect:/admin/movies";

        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movie-edit"; // Bạn cần có file movie-edit.html
    }

    // 2. Xử lý lưu phim đã sửa
    @PostMapping("/movies/update")
    public String handleUpdate(@ModelAttribute Movie movie, HttpSession session, RedirectAttributes ra) {
        if (getAdmin(session) == null) return "redirect:/login";

        try {
            // Rất quan trọng: Kiểm tra xem phim có tồn tại trong DB không trước khi lưu
            Movie existingMovie = movieService.getMovieById(movie.getId());
            if (existingMovie == null) {
                ra.addFlashAttribute("error", "Không tìm thấy phim để cập nhật!");
                return "redirect:/admin/movies";
            }

            // Map lại thể loại
            if (movie.getGenre() != null && movie.getGenre().getId() != null) {
                movie.setGenre(genreRepository.findById(movie.getGenre().getId()).orElse(null));
            }

            movieService.saveMovie(movie); // Hibernate sẽ tự động Update vì đã có ID
            ra.addFlashAttribute("success", "Cập nhật phim thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }
}
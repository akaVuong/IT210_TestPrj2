package org.example.it210_project.repository;

import org.example.it210_project.model.Ticket;
import org.example.it210_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // CORE-07: Lấy lịch sử đặt vé của một User, sắp xếp cái mới nhất lên đầu
    List<Ticket> findByUserOrderByBookingTimeDesc(User user);
}
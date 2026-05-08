package org.example.it210_project.repository;

import org.example.it210_project.model.TicketDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TicketDetailRepository extends JpaRepository<TicketDetail, Long> {

    // CORE-06: Kiểm tra xem ghế đã bị đặt trong suất chiếu này chưa
    @Query("SELECT COUNT(td) > 0 FROM TicketDetail td " +
            "WHERE td.ticket.showtime.id = :showtimeId " +
            "AND td.seat.id = :seatId " +
            "AND td.ticket.status = 'BOOKED'")
    boolean isSeatBooked(@Param("showtimeId") Long showtimeId, @Param("seatId") Long seatId);

    // CORE-09: Xóa chi tiết ghế khi hủy vé để giải phóng slot
    void deleteByTicketId(Long ticketId);

    // Lấy danh sách ID ghế đã đặt để hiển thị lên sơ đồ (màu đỏ)
    @Query("SELECT td.seat.id FROM TicketDetail td WHERE td.ticket.showtime.id = :showtimeId AND td.ticket.status = 'BOOKED'")
    List<Long> findBookedSeatIdsByShowtime(@Param("showtimeId") Long showtimeId);
}
package org.example.it210_project.service;

import org.example.it210_project.model.*;
import org.example.it210_project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    @Autowired private TicketRepository ticketRepository;
    @Autowired private TicketDetailRepository detailRepository;
    @Autowired private SeatRepository seatRepository;

    @Transactional // CORE-06: Đảm bảo "Được ăn cả, ngã về không"
    public Ticket createBooking(User user, Showtime showtime, List<Long> seatIds) {
        // 1. Kiểm tra lại một lần nữa xem có ghế nào vừa bị đặt mất không
        for (Long seatId : seatIds) {
            if (detailRepository.isSeatBooked(showtime.getId(), seatId)) {
                throw new RuntimeException("Ghế bạn chọn đã có người vừa đặt mất!");
            }
        }

        // 2. Tạo Ticket (Hóa đơn)
        Ticket ticket = Ticket.builder()
                .user(user)
                .showtime(showtime)
                .bookingTime(LocalDateTime.now())
                .status("BOOKED")
                .totalAmount(showtime.getMovie().getPrice() * seatIds.size())
                .build();
        Ticket savedTicket = ticketRepository.save(ticket);

        // 3. Lưu chi tiết từng ghế vào TicketDetail
        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId).orElseThrow();
            TicketDetail detail = new TicketDetail();
            detail.setTicket(savedTicket);
            detail.setSeat(seat);
            detailRepository.save(detail);
        }
        return savedTicket;
    }

    @Transactional // CORE-09: Hủy vé và giải phóng ghế
    public void cancelBooking(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();

        // Kiểm tra điều kiện 24h
        if (LocalDateTime.now().isAfter(ticket.getShowtime().getStartTime().minusHours(24))) {
            throw new RuntimeException("Chỉ được hủy vé trước 24 giờ!");
        }

        ticket.setStatus("CANCELLED");
        ticketRepository.save(ticket);

        // Giải phóng ghế bằng cách xóa chi tiết trong TicketDetail
        detailRepository.deleteByTicketId(ticketId);
    }
}
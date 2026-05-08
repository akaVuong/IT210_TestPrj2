package org.example.it210_project.service;

import org.example.it210_project.model.Showtime;
import org.example.it210_project.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowtimeService {
    @Autowired private ShowtimeRepository showtimeRepository;

    public boolean hasConflict(Long roomId, LocalDateTime newStart, LocalDateTime newEnd) {
        // Cộng thêm 15 phút dọn dẹp vào thời gian kết thúc của suất chiếu mới
        LocalDateTime newEndWithCleanup = newEnd.plusMinutes(15);

        // Lấy danh sách suất chiếu của phòng đó
        List<Showtime> existingShowtimes = showtimeRepository.findByRoomId(roomId);

        for (Showtime ex : existingShowtimes) {
            // Suất chiếu cũ cũng cần 15 phút dọn dẹp sau khi kết thúc
            LocalDateTime exEndWithCleanup = ex.getEndTime().plusMinutes(15);

            // Thuật toán kiểm tra giao thoa:
            // Nếu (Bắt đầu mới < Kết thúc cũ + 15p) VÀ (Kết thúc mới + 15p > Bắt đầu cũ)
            if (newStart.isBefore(exEndWithCleanup) && newEndWithCleanup.isAfter(ex.getStartTime())) {
                return true; // Xung đột!
            }
        }
        return false;
    }
}
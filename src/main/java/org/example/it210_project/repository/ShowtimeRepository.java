package org.example.it210_project.repository;

import org.example.it210_project.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // CORE-05: Tìm các suất chiếu bị trùng lịch trong cùng 1 phòng
    // Logic: Một suất chiếu bị trùng nếu (Bắt đầu mới < Kết thúc cũ) AND (Kết thúc mới > Bắt đầu cũ)
    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
            "AND (:newStart < s.endTime AND :newEnd > s.startTime)")
    List<Showtime> findOverlappingShowtimes(@Param("roomId") Long roomId,
                                            @Param("newStart") LocalDateTime newStart,
                                            @Param("newEnd") LocalDateTime newEnd);

    // CORE-08: Lấy danh sách suất chiếu còn hiệu lực (chưa chiếu) cho người dùng xem
    List<Showtime> findByStartTimeAfterAndStatusOrderByStartTimeAsc(LocalDateTime now, String status);
    List<Showtime> findByRoomId(Long roomId);
}
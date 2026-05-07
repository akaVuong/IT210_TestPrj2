package org.example.it210_project.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seatNumber;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
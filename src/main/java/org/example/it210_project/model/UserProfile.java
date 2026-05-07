package org.example.it210_project.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    private Long id; // Trùng với User ID (quan hệ 1-1)

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String phoneNumber;
    private String address;
    private LocalDate birthday;
    private String gender;
}
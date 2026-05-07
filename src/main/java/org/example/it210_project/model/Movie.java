package org.example.it210_project.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration;

    private String posterUrl;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id")
    private Genre genre;
}
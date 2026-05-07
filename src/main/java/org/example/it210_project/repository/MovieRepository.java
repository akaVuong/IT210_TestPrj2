package org.example.it210_project.repository;
import org.example.it210_project.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//CRUD danh sách phim cho Admin (CORE-04)
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {}
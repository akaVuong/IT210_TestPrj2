package org.example.it210_project.service;

import org.example.it210_project.model.*;
import org.example.it210_project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired private GenreRepository genreRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;

    @Override
    public void run(String... args) {
        if (genreRepository.count() == 0) {
            genreRepository.save(new Genre(null, "Hành động"));
            genreRepository.save(new Genre(null, "Kinh dị"));
        }
        if (userRepository.count() == 0) {
            userRepository.save(new User(null, "admin", "123", "admin@cinema.com", "Quản trị viên", User.Role.ADMIN));
            userRepository.save(new User(null, "staff01", "123", "staff1@cinema.com", "NV Bán Vé 01", User.Role.STAFF));
            userRepository.save(new User(null, "staff02", "123", "staff2@cinema.com", "NV Bán Vé 02", User.Role.STAFF));
        }
        if (roomRepository.count() == 0) {
            roomRepository.save(new Room(null, "Phòng 01", 50));
        }
    }
}
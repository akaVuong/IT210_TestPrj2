package org.example.it210_project.service;

import org.example.it210_project.model.User;
import org.example.it210_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // CORE-01: Đăng ký (Tạm thời lưu pass thuần, sau này sẽ hash)
    public User register(User user) {
        return userRepository.save(user);
    }

    // CORE-01: Kiểm tra đăng nhập
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }

    // CORE-03: Cập nhật hồ sơ
    public User updateProfile(User user) {
        return userRepository.save(user);
    }
}
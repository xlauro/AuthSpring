package com.laurosantos.authspring.user;

import com.laurosantos.authspring.exception.NotFoundException;
import com.laurosantos.authspring.user.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }
}

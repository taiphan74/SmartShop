package com.ptithcm.SmartShop.user.service;

import com.ptithcm.SmartShop.user.model.User;
import com.ptithcm.SmartShop.user.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User update(UUID id, User payload) {
        User existing = getById(id);
        existing.setUsername(payload.getUsername());
        existing.setEmail(payload.getEmail());
        existing.setHashedPassword(payload.getHashedPassword());
        existing.setFirstName(payload.getFirstName());
        existing.setLastName(payload.getLastName());
        existing.setGender(payload.getGender());
        existing.setAvatar(payload.getAvatar());
        existing.setPhone(payload.getPhone());
        existing.setAddress(payload.getAddress());
        existing.setCity(payload.getCity());
        existing.setProvince(payload.getProvince());
        existing.setCountry(payload.getCountry());
        existing.setLastLoginAt(payload.getLastLoginAt());
        existing.setActive(payload.isActive());
        existing.setDeletedAt(payload.getDeletedAt());
        return userRepository.save(existing);
    }

    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}

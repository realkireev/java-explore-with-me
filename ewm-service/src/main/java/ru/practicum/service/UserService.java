package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserRequestDto;
import ru.practicum.dto.UserResponseDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repo.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDto> getUsers(List<Long> ids, int from, int size) {
        List<User> users;
        Pageable pageable = PageRequest.of(from / size, size);

        if (ids == null) {
            users = userRepository.findAll(pageable).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }

        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        return userMapper.toUserResponseDto(userRepository.saveAndFlush(userMapper.toUser(userRequestDto)));
    }

    public void deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteById(userId);
    }

    public User findUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throwObjectNotFoundException(userId);
        }

        return optionalUser.get();
    }

    public void existUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throwObjectNotFoundException(userId);
        }
    }

    private void throwObjectNotFoundException(Long userId) {
        throw new ObjectNotFoundException("User with id=%d was not found", userId);
    }
}

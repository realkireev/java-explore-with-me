package ru.practicum.service.interfaces;

import ru.practicum.dto.UserRequestDto;
import ru.practicum.dto.UserResponseDto;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers(List<Long> ids, int from, int size);

    UserResponseDto saveUser(UserRequestDto userRequestDto);

    void deleteUser(Long userId);

    User findUserById(Long userId);
}

package ru.practicum.service.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.CustomPageRequest;
import ru.practicum.dto.UserRequestDto;
import ru.practicum.dto.UserResponseDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repo.UserRepository;
import ru.practicum.service.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.common.Variables.USER_WAS_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getUsers(List<Long> ids, int from, int size) {
        List<User> users;
        Pageable pageable = CustomPageRequest.of(from, size);

        if (ids == null) {
            users = userRepository.findAll(pageable).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }

        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        return userMapper.toUserResponseDto(userRepository.saveAndFlush(userMapper.toUser(userRequestDto)));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(USER_WAS_NOT_FOUND_MESSAGE, userId));
    }
}

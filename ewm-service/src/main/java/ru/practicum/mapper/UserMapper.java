package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.UserRequestDto;
import ru.practicum.dto.UserResponseDto;
import ru.practicum.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUser(UserRequestDto userRequestDto);
}

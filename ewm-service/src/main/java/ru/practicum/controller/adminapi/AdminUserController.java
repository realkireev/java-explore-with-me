package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.UserRequestDto;
import ru.practicum.dto.UserResponseDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.SIZE_DEFAULT;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getUsersByIds(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = FROM_DEFAULT) @Min(value = 0, message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Min(value = 0, message = SIZE_BELOW_ZERO_MESSAGE) int size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto saveUser(@Valid @NotNull @RequestBody UserRequestDto userRequestDto) {
        return userService.saveUser(userRequestDto);
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}

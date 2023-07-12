package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ConfirmResponseDto {
    private List<RequestResponseDto> confirmedRequests;
    private List<RequestResponseDto> rejectedRequests;
}

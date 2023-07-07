package ru.practicum.shareit.request;

import javax.validation.constraints.NotBlank;

public class ItemRequestDto {
    @NotBlank
    private String description;

}

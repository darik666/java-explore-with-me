package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class NewCommentDto {

    @NotNull
    private Long eventId;

    @NotBlank
    @Size(min = 3, max = 777)
    private String text;
}
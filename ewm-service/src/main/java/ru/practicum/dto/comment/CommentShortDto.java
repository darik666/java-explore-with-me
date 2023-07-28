package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentShortDto {

    private Long id;
    private EventShortDto event;
    private UserShortDto author;
    private String text;
    @JsonFormat(pattern = Constants.DATETIMEFORMAT)
    private LocalDateTime created;
}
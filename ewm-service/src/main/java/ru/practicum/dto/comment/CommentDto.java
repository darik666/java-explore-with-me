package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.service.Constants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {
    private Long id;
    private Event event;
    private User author;
    private String text;
    @JsonFormat(pattern = Constants.DATETIMEFORMAT)
    private LocalDateTime created;
}

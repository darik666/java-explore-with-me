package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.Comment;
import ru.practicum.service.Constants;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto extends EventShortDto {

    @JsonFormat(pattern = Constants.DATETIMEFORMAT)
    private String createdOn;

    private String description;
    private Location location;
    private Integer participantLimit = 0;

    @JsonFormat(pattern = Constants.DATETIMEFORMAT)
    private String publishedOn;

    private boolean requestModeration = true;
    private String state;
    private List<Comment> comments;
}
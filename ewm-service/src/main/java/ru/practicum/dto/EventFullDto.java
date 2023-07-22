package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdOn;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;

    private UserShortDto initiator;
    private Location location;
    private boolean paid;

    private Integer participantLimit = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String publishedOn;

    private boolean requestModeration = true;
    private String state;
    private String title;
    private Integer views;
}

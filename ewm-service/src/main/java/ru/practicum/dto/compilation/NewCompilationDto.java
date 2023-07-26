package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    @UniqueElements
    private List<Long> events;

    private boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    public Boolean getPinned() {
        return pinned;
    }
}
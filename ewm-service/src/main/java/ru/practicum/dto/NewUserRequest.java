package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @Size(min = 6, max = 254)
    private String email;

    @Size(min = 2, max = 250)
    private String name;
}

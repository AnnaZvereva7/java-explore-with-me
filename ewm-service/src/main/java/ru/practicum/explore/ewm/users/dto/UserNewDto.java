package ru.practicum.explore.ewm.users.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserNewDto {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}

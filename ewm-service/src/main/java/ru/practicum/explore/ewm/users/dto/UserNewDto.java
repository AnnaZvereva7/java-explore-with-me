package ru.practicum.explore.ewm.users.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserNewDto {
    @NotBlank(message = "Name must be not blank")
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank(message = "Email must be not blank")
    @Email(message = "Wrong email pattern")
    @Size(min = 6, max = 254)
    private String email;
}

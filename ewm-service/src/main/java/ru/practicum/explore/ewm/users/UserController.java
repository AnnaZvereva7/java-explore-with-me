package ru.practicum.explore.ewm.users;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.users.dto.UserDto;
import ru.practicum.explore.ewm.users.dto.UserNewDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Validated
public class UserController {

    @GetMapping
    public List<UserDto> getUsers(@RequestParam (required = false) Long[] ids,
                                  @RequestParam (defaultValue = "0") int from,
                                  @RequestParam (defaultValue = "10") int size) {
        return  null;
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserNewDto newUser) {
        return null;
    }

    @DeleteMapping("/userId")
    public void delete(@PathVariable Long userId) {

    }

}

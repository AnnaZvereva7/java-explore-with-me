package ru.practicum.explore.ewm.users;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUserByIdIn(List<Long> usersId, OffsetBasedPageRequest pageRequest);

}

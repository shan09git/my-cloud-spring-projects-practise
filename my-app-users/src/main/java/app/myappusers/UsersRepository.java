package app.myappusers;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

interface UsersRepository extends ListCrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findUserEntityByEmail(String email);
}

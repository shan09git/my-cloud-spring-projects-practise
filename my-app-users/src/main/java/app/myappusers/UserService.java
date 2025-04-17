package app.myappusers;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

interface UserService extends UserDetailsService {

    List<UserDto> findAll();

    UserDto createNewUser(UserDto userDto);


}

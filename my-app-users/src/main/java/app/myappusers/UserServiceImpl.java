package app.myappusers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public List<UserDto> findAll() {
        return this.usersRepository
                .findAll()
                .stream()
                .map(entity -> {
                    var mapper = new ModelMapper();
                    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    return mapper.map(entity, UserDto.class);
                })
                .toList();
    }

    @Override
    public UserDto createNewUser(UserDto userDto) {
        userDto.setId(UsersKeyGenerator.generateUUIDKey());
        userDto.setEncryptedPassword(this.bCryptPasswordEncoder.encode(userDto.getPassword()));
        var mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        var entity = this.usersRepository.save(mapper.map(userDto, UserEntity.class));
        return mapper.map(entity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //System.out.println("got the email  here in load by username  = " + username);
        var userEntity = this.usersRepository.findByEmail(username)
                .orElseThrow();

        //System.out.println("userEntity.toString() = " + userEntity.toString());
//        System.out.println("authUser = " +
//                "user " + authUser.getUsername() + "auth" + authUser.getAuthorities() );

        return new AuthUser(userEntity);

    }
}

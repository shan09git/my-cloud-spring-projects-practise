package app.myappusers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
class UserController {

    private final Environment environment;
    private final UserService userService;

    public UserController(Environment environment, UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }

    @GetMapping("/status/check")
    String getStatus() {
        return "up and running on port " + this.environment.getProperty("local.server.port");
    }

    @PostMapping
    ResponseEntity<UserResponseModel> save(@RequestBody UserRequestModel userRequestModel) {
        var mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        var dto = this.userService.createNewUser(mapper.map(userRequestModel, UserDto.class));
        var response = mapper.map(dto, UserResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/get/")
    ResponseEntity<List<UserResponseModel>> findAll() {
        var resposneList = this.userService.findAll()
                .stream()
                .map(dto -> {
                    var mapper = new ModelMapper();
                    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    return mapper.map(dto, UserResponseModel.class);
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(resposneList);
    }
}

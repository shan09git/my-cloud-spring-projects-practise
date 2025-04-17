package app.myappusers;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
class UserDto {

    private String id;

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String encryptedPassword;

}

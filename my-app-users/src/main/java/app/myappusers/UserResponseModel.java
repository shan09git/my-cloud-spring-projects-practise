package app.myappusers;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseModel {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;

}

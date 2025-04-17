package app.myappusers;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthModel {

    private String email;
    private String password;


}

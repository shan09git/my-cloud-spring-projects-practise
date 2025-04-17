package app.myappusers;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
class UserEntity{

    @Id
    private String id;

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String encryptedPassword;

}

package cloud.ptl.povserver.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "user")
@Table(name = "user")
@Data
public class UserDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private boolean isActive;
}

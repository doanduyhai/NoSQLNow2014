package info.archinnov.achilles.demo.entity;

import info.archinnov.achilles.annotations.Column;
import info.archinnov.achilles.annotations.Entity;
import info.archinnov.achilles.annotations.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@Entity(table = User.TABLE_NAME, comment = "users table")
public class User {

    public static final String TABLE_NAME = "users";

    @Id
    private String login;

    @Column
    private String firstname;

    @Column
    private String lastname;

    public User(String login, String firstname, String lastname) {
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}

package org.spring.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{username.notnull}")
    @Size(min = 3, max = 20, message = "{username.size}")
    @Column(unique = true)
    private String username;

    @NotNull(message = "{password.notnull}")
    @Size(min = 6, message = "{password.size}")
    private String password;

    @NotNull(message = "{email.notnull}")
    @Email(message = "{email.email}")
    private String email;
}

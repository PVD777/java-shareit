package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.utility.Generated;

import javax.persistence.*;

@Generated
@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(unique =  true)
    String name;
    @Column(unique =  true)
    String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.utility.Generated;

import javax.persistence.*;

@Generated
@Data
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

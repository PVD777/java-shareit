package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utility.Generated;

import javax.persistence.*;
import java.time.LocalDateTime;

@Generated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    LocalDateTime createdDateTime;

    public ItemRequest(String description, User user, LocalDateTime createdDateTime) {
        this.description = description;
        this.user = user;
        this.createdDateTime = createdDateTime;
    }
}

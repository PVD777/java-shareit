package ru.practicum.shareit.comment.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String text;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User author;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
    @Column(name = "comment_day_time")
    LocalDateTime create;
}

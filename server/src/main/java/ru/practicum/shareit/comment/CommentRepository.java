package ru.practicum.shareit.comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentsByItemIdOrderByCreate(int itemId);

    @Query(value = "SELECT comment FROM Comment comment WHERE comment.item.id IN :itemIds")
    List<Comment> findCommentsByItemIdInOrderById(@Param("itemIds") List<Integer> itemIds, Sort sort);
}

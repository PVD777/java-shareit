package ru.practicum.shareit.item.dao;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findItemsByOwnerIdOrderById(int ownerId, Pageable pageable);

    @Query(value = "select * " +
            "from items " +
            "where available and (" +
            "   (lower(name) like lower(concat('%', :text, '%'))) " +
            "       or (lower(description) like lower(concat('%', :text, '%')))" +
            ")", nativeQuery = true)
    List<Item> findItemsByNameOrDescription(@Param("text") String text, Pageable pageable);

    List<Item> getItemByRequestId(int requestId);

    List<Item> findByRequestIdIn(List<Integer> requestIds);
}
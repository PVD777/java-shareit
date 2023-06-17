package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findItemsByOwnerIdOrderById(int ownerId, Pageable pageable);

    List<Item> findItemsByAvailableIsTrueAndDescriptionContainsIgnoreCase(String text, Pageable pageable);

    List<Item> getItemByRequestId(int requestId);
}
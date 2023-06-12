package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> getItemsByOwnerId(int ownerId);


    List<Item> getItemByAvailableIsTrueAndDescriptionContainsIgnoreCase(String text);
}
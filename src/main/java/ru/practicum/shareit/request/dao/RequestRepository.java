package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {

    Collection<ItemRequest> getItemRequestsByUserIdOrderByCreatedDateTime(int userId);

}

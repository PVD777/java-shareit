package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {

    Collection<ItemRequest> getItemRequestsByUserIdOrderByCreatedDateTime(int userId);

    List<ItemRequest> findItemRequestsByUserIdNotOrderByCreatedDateTime(int userId, Pageable pageable);
}

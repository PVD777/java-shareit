package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(validateUser(userId));
        if (itemDto.getRequestId() != null) {
            ItemRequest request = validateRequest(itemDto.getRequestId());
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(int itemId, int userId) {
        Item item = validateItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            Map<Integer, List<Booking>> bookingsGroupByItemIds = getBookingsByItemIds(List.of(itemId));
            Booking lastBooking = getLastBooking(bookingsGroupByItemIds, itemId);
            Booking nextBooking = getNextBooking(bookingsGroupByItemIds, itemId);
            if (lastBooking != null)
                itemDto.setLastBooking(BookingMapper.bookingToDtoOutShort(lastBooking));
            if (nextBooking != null)
                itemDto.setNextBooking(BookingMapper.bookingToDtoOutShort(nextBooking));
        }
        itemDto.setComments(commentRepository.findCommentsByItemIdOrderByCreate(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item oldItem = validateItem(itemId);
        Item updatedItem = ItemMapper.dtoToItem(itemDto);
        if (oldItem.getOwner().getId() != userId) {
            throw new ForbiddenException("Доступ запрещен");
        }
        if (updatedItem.getName() != null) {
            oldItem.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            oldItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            oldItem.setAvailable(updatedItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> getOwnersItem(int userId, Pageable pageable) {
        List<Item> items = itemRepository.findItemsByOwnerIdOrderById(userId, pageable);
        List<Integer> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Integer, List<Comment>> commentsByItemId = getCommentsByItemIds(itemsId);
        Map<Integer, List<Booking>> bookingsGroupByItemIds = getBookingsByItemIds(itemsId);

        return items
                .stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> itemDto.setComments(commentsByItemId.getOrDefault(itemDto.getId(),
                                Collections.emptyList()).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())))
                .peek(itemDto -> itemDto.setLastBooking(BookingMapper
                        .bookingToDtoOutShort(getLastBooking(bookingsGroupByItemIds, itemDto.getId()))))
                .peek(itemDto -> itemDto.setNextBooking(BookingMapper
                        .bookingToDtoOutShort(getNextBooking(bookingsGroupByItemIds, itemDto.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItems(String text, Pageable pageable) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.findItemsByNameOrDescription(text,
                        pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        Item item = validateItem(itemId);
        User user = validateUser(userId);
        boolean wasBooked = bookingRepository.findBookingsByItemId(itemId, LocalDateTime.now()).stream()
                .anyMatch(booking -> booking.getUser().getId() == userId);
        if (!wasBooked) {
            throw new ValidationException("Доступ запрещен");
        }
        Comment comment = CommentMapper.dtoToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreate(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Map<Integer, List<Comment>> getCommentsByItemIds(List<Integer> itemIds) {
        Map<Integer, List<Comment>> commentsGroupByItemIds = new HashMap<>();
        List<Comment> comments = commentRepository
                .findCommentsByItemIdInOrderById(itemIds, Sort.by(Sort.Direction.ASC, "id"));
        for (Comment comment : comments) {
            Integer itemId = comment.getItem().getId();
            List<Comment> commentsByItemId = commentsGroupByItemIds.get(itemId);
            if (commentsByItemId == null) {
                commentsByItemId = new ArrayList<>();
            }
            commentsByItemId.add(comment);
            commentsGroupByItemIds.put(itemId, commentsByItemId);
        }
        return commentsGroupByItemIds;
    }

    private Map<Integer, List<Booking>> getBookingsByItemIds(List<Integer> itemIds) {
        List<Booking> bookings = bookingRepository.findBookingsByItemIdIn(itemIds);
        return bookings.stream()
                .collect(groupingBy(i -> i.getItem().getId(), toList()));
    }

    private Booking getLastBooking(Map<Integer, List<Booking>> bookingsGroupByItemIds, Integer itemId) {
        List<Booking> bookings = bookingsGroupByItemIds.get(itemId);
        Booking lastBooking = null;
        if (bookings != null) {
            List<Booking> lastBookings = bookings
                    .stream()
                    .filter(booking -> booking.getBookingStart().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                    .collect(Collectors.toList());
            if (!lastBookings.isEmpty())
                lastBooking = lastBookings.get(0);
        }
        return lastBooking;
    }

    private Booking getNextBooking(Map<Integer, List<Booking>> bookingsGroupByItemIds, Integer itemId) {
        List<Booking> bookings = bookingsGroupByItemIds.get(itemId);
        Booking nextBooking = null;
        if (bookings != null) {
            List<Booking> nextBookings = bookings
                    .stream()
                    .filter(booking -> booking.getBookingStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getBookingStart))
                    .collect(Collectors.toList());
            if (!nextBookings.isEmpty())
                nextBooking = nextBookings.get(0);
        }
        return nextBooking;
    }

    private Item validateItem(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный item не найден"));
    }

    private User validateUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный User не найден"));
    }

    private ItemRequest validateRequest(int requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный Request не найден"));
    }

}



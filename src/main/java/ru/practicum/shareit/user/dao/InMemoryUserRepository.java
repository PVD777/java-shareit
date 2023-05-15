package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private int userIdCounter = 0;
    private final HashMap<Integer, User> users = new HashMap<>();
    private final Set<String> nameSet = new HashSet<>();
    private final Set<String> emailSet = new HashSet<>();

    @Override
    public User createUser(User user) {
        user.setId(++userIdCounter);
        users.put(userIdCounter,user);
        nameSet.add(user.getName());
        emailSet.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> getUser(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User updateUser(int id, User user) {
        User oldUser = users.get(id);
        updateNameEmailSet(nameSet,oldUser.getName(),user.getName());
        updateNameEmailSet(emailSet,oldUser.getEmail(),user.getEmail());
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        nameSet.remove(users.get(id).getName());
        emailSet.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public boolean isExist(String name, String email) {
        if ((name != null && nameSet.contains(name)) ||
                (email != null && emailSet.contains(email))) {
            return true;
        }
        return false;
    }


    private void updateNameEmailSet(Set<String> set, String oldData, String newData) {
        if (!oldData.equals(newData)) {
            set.remove(oldData);
            set.add(newData);
        }
    }
}

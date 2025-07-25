package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public User save(User user) {
        if (isUnavailableEmail(user.getEmail())) {
            throw new DuplicatedDataException("Email", user.getEmail());
        }
        if (user.getId() == null) {
            user.setId(idCounter.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id)).map(User::copyOf);
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().map(User::copyOf).toList();
    }

    @Override
    public User update(User user) {
        User userToUpdate = users.get(user.getId());

        if (!user.getEmail().equalsIgnoreCase(userToUpdate.getEmail()) &&
                isUnavailableEmail(user.getEmail())) {
            throw new DuplicatedDataException("Email", user.getEmail());
        }

        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setName(user.getName());

        return userToUpdate.copyOf();
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public void clear() {
        users.clear();
        idCounter.set(0);
    }

    private boolean isUnavailableEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}

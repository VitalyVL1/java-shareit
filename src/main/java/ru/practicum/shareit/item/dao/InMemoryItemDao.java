package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryItemDao implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> itemsByUserId = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter.getAndIncrement());
        }

        items.put(item.getId(), item);
        itemsByUserId.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>())
                .add(item);

        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id)).map(Item::copyOf);
    }

    @Override
    public List<Item> findAll() {
        return items.values().stream()
                .map(Item::copyOf)
                .toList();
    }

    @Override
    public List<Item> findByUser(User user) {
        List<Item> userItems = itemsByUserId.get(user.getId());
        return userItems == null ? Collections.emptyList()
                : userItems.stream()
                .map(Item::copyOf)
                .toList();
    }

    @Override
    public List<Item> search(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        String lowerQuery = query.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerQuery) ||
                        item.getDescription().toLowerCase().contains(lowerQuery))
                .map(Item::copyOf)
                .toList();
    }

    @Override
    public Item update(Item item) {
        Item itemToUpdate = items.get(item.getId());
        itemToUpdate.setName(item.getName());
        itemToUpdate.setDescription(item.getDescription());
        itemToUpdate.setAvailable(item.getAvailable());
        return itemToUpdate;
    }

    @Override
    public void deleteById(Long id) {
        Item item = items.remove(id);
        if (item != null) {
            itemsByUserId.getOrDefault(item.getOwner().getId(), Collections.emptyList())
                    .removeIf(i -> i.getId().equals(id));
        }
    }

    @Override
    public void clear() {
        items.clear();
        itemsByUserId.clear();
        idCounter.set(0);
    }
}

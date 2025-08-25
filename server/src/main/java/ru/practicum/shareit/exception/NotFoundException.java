package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String entityName;
    private final Long entityId;

    public NotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " not found");
        this.entityName = entityName;
        this.entityId = id;
    }
}

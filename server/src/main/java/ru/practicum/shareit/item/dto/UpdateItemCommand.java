package ru.practicum.shareit.item.dto;

/**
 * Команда для обновления существующей вещи в модуле server.
 * <p>
 * Объединяет все необходимые данные для обновления вещи в одном объекте:
 * идентификатор пользователя-владельца, идентификатор обновляемой вещи
 * и DTO с данными для обновления. Используется для передачи данных между
 * слоями приложения и инкапсуляции параметров операции.
 * </p>
 *
 * @param userId     идентификатор пользователя-владельца вещи
 * @param itemId     идентификатор обновляемой вещи
 * @param updateData DTO с обновляемыми полями (название, описание, доступность)
 *
 * @see ItemUpdateDto
 * @see ru.practicum.shareit.item.service.ItemService
 * @see ru.practicum.shareit.item.ItemController
 */
public record UpdateItemCommand(
        Long userId,
        Long itemId,
        ItemUpdateDto updateData
) {
    /**
     * Статический фабричный метод для создания команды обновления вещи.
     * <p>
     * Предоставляет удобный способ создания экземпляра команды без явного вызова конструктора.
     * </p>
     *
     * @param userId     идентификатор пользователя-владельца вещи
     * @param itemId     идентификатор обновляемой вещи
     * @param updateData DTO с обновляемыми полями
     * @return новый экземпляр {@link UpdateItemCommand}
     */
    public static UpdateItemCommand of(Long userId, Long itemId, ItemUpdateDto updateData) {
        return new UpdateItemCommand(userId, itemId, updateData);
    }
}
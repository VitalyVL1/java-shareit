package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link Item} в базе данных.
 * <p>
 * Предоставляет методы для выполнения операций с вещами, включая
 * поиск по владельцу, текстовый поиск доступных вещей,
 * а также поиск вещей, связанных с запросами.
 * </p>
 *
 * @see Item
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Находит все вещи, принадлежащие указанному пользователю.
     *
     * @param userId идентификатор владельца вещей
     * @return список вещей пользователя
     */
    List<Item> findAllByOwnerId(Long userId);

    /**
     * Выполняет поиск доступных вещей по тексту в названии или описании.
     * <p>
     * Поиск регистронезависимый, возвращаются только вещи с флагом available = true.
     * </p>
     *
     * @param text текст для поиска (может быть частичным совпадением)
     * @return список доступных вещей, название или описание которых содержит указанный текст
     */
    @Query("""
            SELECT i FROM Item i
            WHERE i.available = true
            AND (
                lower(i.name) LIKE lower(concat('%', :text, '%'))
                OR lower(i.description) LIKE lower(concat('%', :text, '%'))
            )
            """)
    List<Item> search(@Param("text") String text);

    /**
     * Находит все вещи, созданные в ответ на указанный запрос.
     *
     * @param requestId идентификатор запроса
     * @return список вещей, созданных по данному запросу
     */
    List<Item> findAllByRequestId(Long requestId);

    /**
     * Находит все вещи, созданные в ответ на запросы конкретного пользователя.
     *
     * @param requestorId идентификатор пользователя, создавшего запросы
     * @return список вещей, созданных по запросам указанного пользователя
     */
    List<Item> findAllByRequest_Requestor_Id(Long requestorId);

    /**
     * Находит все вещи, которые были созданы в ответ на какие-либо запросы.
     * <p>
     * Возвращает вещи, у которых поле request не равно null.
     * </p>
     *
     * @return список вещей, связанных с запросами
     */
    List<Item> findAllByRequestNotNull();
}
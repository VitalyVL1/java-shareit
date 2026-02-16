package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link ItemRequest} в базе данных.
 * <p>
 * Предоставляет методы для выполнения операций с запросами вещей,
 * включая поиск запросов по идентификатору пользователя с сортировкой.
 * </p>
 *
 * @see ItemRequest
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Находит все запросы, созданные указанным пользователем, с применением сортировки.
     * <p>
     * Используется для получения списка запросов конкретного пользователя
     * с возможностью сортировки по заданным критериям (например, по дате создания).
     * </p>
     *
     * @param requestorId идентификатор пользователя, создавшего запросы
     * @param sort        параметры сортировки результатов
     * @return список запросов указанного пользователя, отсортированный в соответствии с параметрами
     */
    List<ItemRequest> findAllByRequestor_Id(Long requestorId, Sort sort);
}
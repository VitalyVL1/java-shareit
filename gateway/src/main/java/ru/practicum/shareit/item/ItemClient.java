package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

/**
 * Клиент для взаимодействия с сервисом вещей на сервере ShareIt.
 * <p>
 * Реализует все операции, связанные с вещами и комментариями:
 * создание, обновление, получение списка вещей владельца,
 * получение конкретной вещи, поиск вещей по тексту и добавление комментариев.
 * </p>
 *
 * @see BaseClient
 * @see ItemCreateDto
 * @see ItemUpdateDto
 * @see CommentCreateOrUpdateDto
 */
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    /**
     * Создает новый экземпляр клиента вещей.
     * <p>
     * Конструктор настраивает {@link RestTemplate} с базовым URL сервера,
     * добавляя префикс "/items" ко всем запросам.
     * </p>
     *
     * @param serverUrl базовый URL сервера ShareIt (из конфигурации shareit-server.url)
     * @param builder   строитель для создания RestTemplate
     */
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Добавляет новую вещь.
     * <p>
     * Соответствует POST-запросу к эндпоинту "/items".
     * </p>
     *
     * @param userId идентификатор владельца вещи (добавляется в заголовок X-Sharer-User-Id)
     * @param dto    DTO с данными для создания вещи (название, описание, доступность, опционально requestId)
     * @return {@link ResponseEntity} с созданной вещью
     */
    public ResponseEntity<Object> addItem(long userId, ItemCreateDto dto) {
        return post("", userId, dto);
    }

    /**
     * Обновляет существующую вещь.
     * <p>
     * Соответствует PATCH-запросу к эндпоинту "/items/{itemId}".
     * </p>
     *
     * @param userId идентификатор владельца вещи (должен совпадать с владельцем)
     * @param itemId идентификатор обновляемой вещи
     * @param dto    DTO с обновляемыми полями (все поля опциональны)
     * @return {@link ResponseEntity} с обновленной вещью
     */
    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemUpdateDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    /**
     * Получает список всех вещей конкретного владельца.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/items".
     * </p>
     *
     * @param userId идентификатор владельца вещей
     * @return {@link ResponseEntity} со списком вещей владельца
     */
    public ResponseEntity<Object> getItemsByOwner(long userId) {
        return get("", userId);
    }

    /**
     * Получает информацию о конкретной вещи по её идентификатору.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/items/{itemId}".
     * </p>
     *
     * @param userId идентификатор пользователя, запрашивающего информацию (для проверки доступности)
     * @param itemId идентификатор вещи
     * @return {@link ResponseEntity} с данными вещи, включая комментарии и даты бронирований (если пользователь - владелец)
     */
    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    /**
     * Выполняет поиск вещей по тексту в названии или описании.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/items/search?text={text}".
     * Поиск доступен только для доступных вещей (available = true).
     * </p>
     *
     * @param text текст для поиска (может быть пустым, тогда вернется пустой список)
     * @return {@link ResponseEntity} со списком найденных вещей
     */
    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, parameters);
    }

    /**
     * Добавляет комментарий к вещи от пользователя, который её арендовал.
     * <p>
     * Соответствует POST-запросу к эндпоинту "/items/{itemId}/comment".
     * Комментарий можно оставить только после завершения бронирования.
     * </p>
     *
     * @param authorId идентификатор автора комментария (пользователь, бравший вещь в аренду)
     * @param itemId   идентификатор вещи, к которой оставляется комментарий
     * @param dto      DTO с текстом комментария
     * @return {@link ResponseEntity} с созданным комментарием
     */
    public ResponseEntity<Object> addComment(long authorId, long itemId, CommentCreateOrUpdateDto dto) {
        return post("/" + itemId + "/comment", authorId, dto);
    }
}
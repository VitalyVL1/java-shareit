package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 * Клиент для взаимодействия с сервисом запросов вещей на сервере ShareIt.
 * <p>
 * Реализует все операции, связанные с запросами вещей:
 * создание запроса, получение запросов конкретного пользователя,
 * получение конкретного запроса по ID, получение всех запросов.
 * </p>
 *
 * @see BaseClient
 * @see ItemRequestCreateDto
 * @see ItemRequestController
 */
@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    /**
     * Создает новый экземпляр клиента запросов.
     * <p>
     * Конструктор настраивает {@link RestTemplate} с базовым URL сервера,
     * добавляя префикс "/requests" ко всем запросам.
     * </p>
     *
     * @param serverUrl базовый URL сервера ShareIt (из конфигурации shareit-server.url)
     * @param builder   строитель для создания RestTemplate
     */
    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Создает новый запрос вещи.
     * <p>
     * Соответствует POST-запросу к эндпоинту "/requests".
     * </p>
     *
     * @param userId идентификатор пользователя, создающего запрос (добавляется в заголовок X-Sharer-User-Id)
     * @param dto    DTO с описанием запрашиваемой вещи
     * @return {@link ResponseEntity} с созданным запросом
     */
    public ResponseEntity<Object> addRequest(long userId, ItemRequestCreateDto dto) {
        return post("", userId, dto);
    }

    /**
     * Получает список всех запросов, созданных конкретным пользователем.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/requests".
     * </p>
     *
     * @param userId идентификатор пользователя
     * @return {@link ResponseEntity} со списком запросов пользователя
     */
    public ResponseEntity<Object> getRequestsByUserId(long userId) {
        return get("", userId);
    }

    /**
     * Получает информацию о конкретном запросе по его идентификатору.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/requests/{requestId}".
     * </p>
     *
     * @param requestId идентификатор запроса
     * @return {@link ResponseEntity} с данными запроса
     */
    public ResponseEntity<Object> getRequestsById(long requestId) {
        return get("/" + requestId);
    }

    /**
     * Получает список всех запросов, созданных другими пользователями.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/requests/all".
     * Используется для просмотра доступных запросов, на которые можно предложить свои вещи.
     * </p>
     *
     * @return {@link ResponseEntity} со списком всех запросов
     */
    public ResponseEntity<Object> getAllRequests() {
        return get("/all");
    }
}
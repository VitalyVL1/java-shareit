package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

/**
 * Клиент для взаимодействия с сервисом пользователей на сервере ShareIt.
 * <p>
 * Реализует все операции, связанные с пользователями:
 * создание, обновление, получение списка всех пользователей,
 * получение конкретного пользователя по ID и удаление пользователя.
 * </p>
 *
 * @see BaseClient
 * @see UserCreateDto
 * @see UserUpdateDto
 * @see UserController
 */
@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    /**
     * Создает новый экземпляр клиента пользователей.
     * <p>
     * Конструктор настраивает {@link RestTemplate} с базовым URL сервера,
     * добавляя префикс "/users" ко всем запросам.
     * </p>
     *
     * @param serverUrl базовый URL сервера ShareIt (из конфигурации shareit-server.url)
     * @param builder   строитель для создания RestTemplate
     */
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Создает нового пользователя.
     * <p>
     * Соответствует POST-запросу к эндпоинту "/users".
     * </p>
     *
     * @param dto DTO с данными для создания пользователя (имя и email)
     * @return {@link ResponseEntity} с созданным пользователем
     */
    public ResponseEntity<Object> addUser(UserCreateDto dto) {
        return post("", dto);
    }

    /**
     * Обновляет существующего пользователя.
     * <p>
     * Соответствует PATCH-запросу к эндпоинту "/users/{id}".
     * </p>
     *
     * @param id  идентификатор обновляемого пользователя
     * @param dto DTO с обновляемыми полями (оба поля опциональны)
     * @return {@link ResponseEntity} с обновленным пользователем
     */
    public ResponseEntity<Object> updateUser(long id, UserUpdateDto dto) {
        return patch("/" + id, dto);
    }

    /**
     * Получает список всех пользователей.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/users".
     * </p>
     *
     * @return {@link ResponseEntity} со списком всех пользователей
     */
    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    /**
     * Получает информацию о конкретном пользователе по его идентификатору.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/users/{id}".
     * </p>
     *
     * @param id идентификатор пользователя
     * @return {@link ResponseEntity} с данными пользователя
     */
    public ResponseEntity<Object> getUserById(long id) {
        return get("/" + id);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * <p>
     * Соответствует DELETE-запросу к эндпоинту "/users/{id}".
     * </p>
     *
     * @param id идентификатор пользователя для удаления
     * @return {@link ResponseEntity} с подтверждением удаления
     */
    public ResponseEntity<Object> deleteUserById(long id) {
        return delete("/" + id);
    }
}
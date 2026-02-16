package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Базовый клиент для выполнения HTTP-запросов к серверу ShareIt.
 * <p>
 * Предоставляет набор удобных методов для выполнения основных HTTP-операций (GET, POST, PUT, PATCH, DELETE)
 * с автоматическим добавлением заголовков, включая идентификатор пользователя (X-Sharer-User-Id).
 * Обрабатывает ошибки и преобразует ответы сервера в формат, подходящий для gateway.
 * </p>
 *
 * @see RestTemplate
 */
public class BaseClient {
    protected final RestTemplate rest;

    /**
     * Создает новый экземпляр базового клиента.
     *
     * @param rest настроенный {@link RestTemplate} для выполнения HTTP-запросов
     */
    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * Выполняет GET-запрос по указанному пути.
     *
     * @param path путь к ресурсу
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    /**
     * Выполняет GET-запрос по указанному пути от имени конкретного пользователя.
     *
     * @param path   путь к ресурсу
     * @param userId идентификатор пользователя (будет добавлен в заголовок X-Sharer-User-Id)
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected ResponseEntity<Object> get(String path, long userId) {
        return get(path, userId, null);
    }

    /**
     * Выполняет GET-запрос по указанному пути с параметрами запроса от имени конкретного пользователя.
     *
     * @param path       путь к ресурсу
     * @param userId     идентификатор пользователя (может быть null)
     * @param parameters параметры запроса в виде карты (например, {"state": "ALL"})
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    /**
     * Выполняет POST-запрос по указанному пути с телом запроса.
     *
     * @param path путь к ресурсу
     * @param body тело запроса
     * @param <T>  тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    /**
     * Выполняет POST-запрос по указанному пути от имени конкретного пользователя с телом запроса.
     *
     * @param path   путь к ресурсу
     * @param userId идентификатор пользователя
     * @param body   тело запроса
     * @param <T>    тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return post(path, userId, null, body);
    }

    /**
     * Выполняет POST-запрос по указанному пути с параметрами и телом запроса от имени конкретного пользователя.
     *
     * @param path       путь к ресурсу
     * @param userId     идентификатор пользователя (может быть null)
     * @param parameters параметры запроса в виде карты
     * @param body       тело запроса
     * @param <T>        тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    /**
     * Выполняет PUT-запрос по указанному пути от имени конкретного пользователя с телом запроса.
     *
     * @param path   путь к ресурсу
     * @param userId идентификатор пользователя
     * @param body   тело запроса
     * @param <T>    тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return put(path, userId, null, body);
    }

    /**
     * Выполняет PUT-запрос по указанному пути с параметрами и телом запроса от имени конкретного пользователя.
     *
     * @param path       путь к ресурсу
     * @param userId     идентификатор пользователя
     * @param parameters параметры запроса в виде карты
     * @param body       тело запроса
     * @param <T>        тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    /**
     * Выполняет PATCH-запрос по указанному пути с телом запроса.
     *
     * @param path путь к ресурсу
     * @param body тело запроса
     * @param <T>  тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    /**
     * Выполняет PATCH-запрос по указанному пути от имени конкретного пользователя без тела запроса.
     *
     * @param path   путь к ресурсу
     * @param userId идентификатор пользователя
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> patch(String path, long userId) {
        return patch(path, userId, null, null);
    }

    /**
     * Выполняет PATCH-запрос по указанному пути от имени конкретного пользователя с телом запроса.
     *
     * @param path   путь к ресурсу
     * @param userId идентификатор пользователя
     * @param body   тело запроса
     * @param <T>    тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return patch(path, userId, null, body);
    }

    /**
     * Выполняет PATCH-запрос по указанному пути с параметрами и телом запроса от имени конкретного пользователя.
     *
     * @param path       путь к ресурсу
     * @param userId     идентификатор пользователя (может быть null)
     * @param parameters параметры запроса в виде карты
     * @param body       тело запроса
     * @param <T>        тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    /**
     * Выполняет DELETE-запрос по указанному пути.
     *
     * @param path путь к ресурсу
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    /**
     * Выполняет DELETE-запрос по указанному пути от имени конкретного пользователя.
     *
     * @param path   путь к ресурсу
     * @param userId идентификатор пользователя
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected ResponseEntity<Object> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    /**
     * Выполняет DELETE-запрос по указанному пути с параметрами запроса от имени конкретного пользователя.
     *
     * @param path       путь к ресурсу
     * @param userId     идентификатор пользователя (может быть null)
     * @param parameters параметры запроса в виде карты
     * @return {@link ResponseEntity} с ответом от сервера
     */
    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    /**
     * Создает и отправляет HTTP-запрос к серверу ShareIt.
     * <p>
     * Внутренний метод, используемый всеми публичными методами для фактической отправки запроса.
     * Добавляет необходимые заголовки, обрабатывает параметры URL и перехватывает исключения.
     * </p>
     *
     * @param method     HTTP-метод (GET, POST, и т.д.)
     * @param path       путь к ресурсу
     * @param userId     идентификатор пользователя (может быть null)
     * @param parameters параметры запроса в виде карты (могут быть null)
     * @param body       тело запроса (может быть null)
     * @param <T>        тип тела запроса
     * @return {@link ResponseEntity} с ответом от сервера или обработанной ошибкой
     */
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    /**
     * Формирует стандартные HTTP-заголовки для запроса к серверу.
     * <p>
     * Устанавливает Content-Type: application/json, Accept: application/json
     * и, если указан идентификатор пользователя, добавляет заголовок X-Sharer-User-Id.
     * </p>
     *
     * @param userId идентификатор пользователя (может быть null)
     * @return {@link HttpHeaders} с заполненными заголовками
     */
    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    /**
     * Подготавливает ответ от сервера для отправки клиенту через gateway.
     * <p>
     * Если статус ответа успешный (2xx), возвращает исходный ответ.
     * В противном случае создает новый ответ с тем же статусом и телом (если оно присутствует).
     * </p>
     *
     * @param response ответ от сервера ShareIt
     * @return подготовленный {@link ResponseEntity} для отправки клиенту
     */
    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

/**
 * Клиент для взаимодействия с сервисом бронирований на сервере ShareIt.
 * <p>
 * Реализует все операции, связанные с бронированиями:
 * создание, подтверждение, получение списка бронирований для пользователя
 * и владельца вещей, просмотр конкретного бронирования.
 * </p>
 *
 * @see BaseClient
 * @see BookingCreateDto
 * @see State
 */
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    /**
     * Создает новый экземпляр клиента бронирований.
     * <p>
     * Конструктор настраивает {@link RestTemplate} с базовым URL сервера,
     * добавляя префикс "/bookings" ко всем запросам.
     * </p>
     *
     * @param serverUrl базовый URL сервера ShareIt (из конфигурации shareit-server.url)
     * @param builder   строитель для создания RestTemplate
     */
    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Получает список бронирований для конкретного пользователя (который бронирует вещи).
     * <p>
     * Поддерживает фильтрацию по статусу бронирования и пагинацию.
     * Соответствует GET-запросу к эндпоинту "/bookings?state={state}&from={from}&size={size}".
     * </p>
     *
     * @param userId идентификатор пользователя (добавляется в заголовок X-Sharer-User-Id)
     * @param state  статус бронирования для фильтрации (ALL, CURRENT, PAST и т.д.)
     * @param from   индекс первого элемента для пагинации (может быть null)
     * @param size   количество элементов на странице (может быть null)
     * @return {@link ResponseEntity} со списком бронирований пользователя
     */
    public ResponseEntity<Object> getBookingsByBooker(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    /**
     * Создает новый запрос на бронирование вещи.
     * <p>
     * Соответствует POST-запросу к эндпоинту "/bookings".
     * </p>
     *
     * @param userId      идентификатор пользователя, создающего бронирование
     * @param requestDto  DTO с данными для создания бронирования (id вещи, даты начала и окончания)
     * @return {@link ResponseEntity} с созданным бронированием
     */
    public ResponseEntity<Object> bookItem(long userId, BookingCreateDto requestDto) {
        return post("", userId, requestDto);
    }

    /**
     * Получает информацию о конкретном бронировании по его идентификатору.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/bookings/{bookingId}".
     * </p>
     *
     * @param userId     идентификатор пользователя (должен быть либо автором бронирования, либо владельцем вещи)
     * @param bookingId  идентификатор бронирования
     * @return {@link ResponseEntity} с данными бронирования
     */
    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    /**
     * Подтверждает или отклоняет запрос на бронирование.
     * <p>
     * Метод для владельца вещи. Соответствует PATCH-запросу к эндпоинту
     * "/bookings/{bookingId}?approved={approved}".
     * </p>
     *
     * @param ownerId    идентификатор владельца вещи
     * @param bookingId  идентификатор бронирования
     * @param approved   true - подтвердить бронирование, false - отклонить
     * @return {@link ResponseEntity} с обновленным бронированием
     */
    public ResponseEntity<Object> approveBooking(long ownerId, long bookingId, Boolean approved) {
        String path = String.format("/%d?approved=%s", bookingId, approved);
        return patch(path, ownerId);
    }

    /**
     * Получает список бронирований для всех вещей конкретного владельца.
     * <p>
     * Соответствует GET-запросу к эндпоинту "/bookings/owner?state={state}&from={from}&size={size}".
     * Поддерживает фильтрацию по статусу и пагинацию.
     * </p>
     *
     * @param userId идентификатор владельца вещей
     * @param state  статус бронирования для фильтрации
     * @param from   индекс первого элемента для пагинации (может быть null)
     * @param size   количество элементов на странице (может быть null)
     * @return {@link ResponseEntity} со списком бронирований для вещей владельца
     */
    public ResponseEntity<Object> getBookingsByOwner(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
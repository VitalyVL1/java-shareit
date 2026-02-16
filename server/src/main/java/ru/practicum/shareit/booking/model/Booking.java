package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность, представляющая бронирование вещи в приложении ShareIt.
 * <p>
 * Содержит информацию о периоде бронирования, связанной вещи, пользователе-арендаторе
 * и текущем статусе бронирования. Является центральной сущностью для функционала аренды.
 * </p>
 *
 * @see Item
 * @see User
 * @see Status
 */
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
@Entity
public class Booking {
    /**
     * Уникальный идентификатор бронирования.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    /**
     * Дата и время начала бронирования.
     * Хранится в базе данных без временной зоны.
     */
    @Column(name = "start_date", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     * Хранится в базе данных без временной зоны.
     */
    @Column(name = "end_date", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime end;

    /**
     * Вещь, на которую оформлено бронирование.
     * Связь многие-к-одному с сущностью {@link Item}.
     * Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;

    /**
     * Пользователь, который бронирует вещь (арендатор).
     * Связь многие-к-одному с сущностью {@link User}.
     * Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    @ToString.Exclude
    private User booker;

    /**
     * Текущий статус бронирования.
     * По умолчанию {@link Status#WAITING}.
     * Хранится в базе данных как строка (EnumType.STRING).
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.WAITING;

    /**
     * Сравнивает объекты Booking по их идентификатору.
     * <p>
     * Переопределен для корректной работы с ленивой загрузкой Hibernate.
     * Два объекта Booking считаются равными, если они имеют одинаковый
     * идентификатор и принадлежат одному классу (учитываются прокси-объекты).
     * </p>
     *
     * @param object объект для сравнения
     * @return {@code true}, если объекты равны, иначе {@code false}
     */
    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Booking booking = (Booking) object;
        return getId() != null && Objects.equals(getId(), booking.getId());
    }

    /**
     * Возвращает хеш-код объекта.
     * <p>
     * Переопределен для корректной работы с ленивой загрузкой Hibernate.
     * Использует хеш-код класса, а не идентификатор, для избежания проблем
     * с еще не сохраненными сущностями.
     * </p>
     *
     * @return хеш-код объекта
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
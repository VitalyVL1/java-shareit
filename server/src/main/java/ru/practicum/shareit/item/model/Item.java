package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

/**
 * Сущность, представляющая вещь в приложении ShareIt.
 * <p>
 * Вещь - основной объект для шеринга. Может быть предложена владельцем для аренды
 * другими пользователями. Содержит информацию о названии, описании, статусе доступности,
 * владельце и связанном запросе (если вещь создана в ответ на запрос).
 * </p>
 *
 * @see User
 * @see ItemRequest
 * @see ru.practicum.shareit.booking.model.Booking
 * @see Comment
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "items")
@Entity
public class Item {
    /**
     * Уникальный идентификатор вещи.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    /**
     * Название вещи.
     */
    @Column
    private String name;

    /**
     * Описание вещи.
     */
    @Column
    private String description;

    /**
     * Флаг доступности вещи для аренды.
     * {@code true} - вещь доступна, {@code false} - недоступна.
     */
    @Column(name = "is_available")
    private Boolean available;

    /**
     * Владелец вещи.
     * Связь многие-к-одному с сущностью {@link User}.
     * Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private User owner;

    /**
     * Запрос, на который отвечает данная вещь.
     * Связь один-к-одному с сущностью {@link ItemRequest}.
     * Может быть {@code null}, если вещь создана не по запросу.
     * Загружается лениво (LAZY).
     */
    @OneToOne
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private ItemRequest request;

    /**
     * Сравнивает объекты Item по их идентификатору.
     * <p>
     * Переопределен для корректной работы с ленивой загрузкой Hibernate.
     * Два объекта Item считаются равными, если они имеют одинаковый
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
        Item item = (Item) object;
        return getId() != null && Objects.equals(getId(), item.getId());
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
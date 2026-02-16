package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

/**
 * Сущность, представляющая пользователя в приложении ShareIt.
 * <p>
 * Пользователь может выступать в нескольких ролях:
 * <ul>
 *   <li>Владелец вещей - добавляет вещи в систему и сдаёт их в аренду</li>
 *   <li>Арендатор - бронирует вещи других пользователей</li>
 *   <li>Автор запросов - создаёт запросы на вещи, которых нет в системе</li>
 *   <li>Автор комментариев - оставляет отзывы на арендованные вещи</li>
 * </ul>
 * </p>
 *
 * @see ru.practicum.shareit.item.model.Item
 * @see ru.practicum.shareit.booking.model.Booking
 * @see ru.practicum.shareit.request.ItemRequest
 * @see ru.practicum.shareit.item.model.Comment
 */
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User {
    /**
     * Уникальный идентификатор пользователя.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * Имя пользователя.
     */
    @Column
    private String name;

    /**
     * Электронная почта пользователя.
     * Должна быть уникальной в системе.
     */
    @Column
    private String email;

    /**
     * Сравнивает объекты User по их идентификатору.
     * <p>
     * Переопределен для корректной работы с ленивой загрузкой Hibernate.
     * Два объекта User считаются равными, если они имеют одинаковый
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
        User user = (User) object;
        return getId() != null && Objects.equals(getId(), user.getId());
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
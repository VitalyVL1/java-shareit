package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность, представляющая запрос вещи в приложении ShareIt.
 * <p>
 * Запрос создается пользователем, когда он хочет найти вещь, которой нет в системе.
 * Другие пользователи могут предлагать свои вещи в ответ на запрос.
 * </p>
 *
 * @see User
 * @see ru.practicum.shareit.item.model.Item
 */
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@Entity
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;

    /**
     * Описание желаемой вещи.
     */
    @Column
    String description;

    /**
     * Пользователь, создавший запрос.
     * Связь многие-к-одному с сущностью {@link User}.
     * Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    @ToString.Exclude
    User requestor;

    /**
     * Дата и время создания запроса.
     * По умолчанию устанавливается текущее время при создании объекта.
     */
    @Builder.Default
    @Column
    LocalDateTime created = LocalDateTime.now();

    /**
     * Сравнивает объекты ItemRequest по их идентификатору.
     * <p>
     * Переопределен для корректной работы с ленивой загрузкой Hibernate.
     * Два объекта ItemRequest считаются равными, если они имеют одинаковый
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
        ItemRequest that = (ItemRequest) object;
        return getId() != null && Objects.equals(getId(), that.getId());
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
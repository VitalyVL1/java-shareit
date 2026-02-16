package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность, представляющая комментарий к вещи в приложении ShareIt.
 * <p>
 * Комментарии могут оставлять пользователи после завершения бронирования вещи.
 * Содержат текст комментария, информацию об авторе и вещи, а также дату создания.
 * </p>
 *
 * @see Item
 * @see User
 */
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
@Entity
public class Comment {
    /**
     * Уникальный идентификатор комментария.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    /**
     * Текст комментария.
     */
    @Column
    private String text;

    /**
     * Вещь, к которой оставлен комментарий.
     * Связь многие-к-одному с сущностью {@link Item}.
     * Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;

    /**
     * Автор комментария.
     * Связь многие-к-одному с сущностью {@link User}.
     * Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    private User author;

    /**
     * Дата и время создания комментария.
     * По умолчанию устанавливается текущее время при создании объекта.
     * Хранится в базе данных без временной зоны.
     */
    @Builder.Default
    @Column(
            name = "created",
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE",
            nullable = false
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime created = LocalDateTime.now();

    /**
     * Сравнивает объекты Comment по их идентификатору.
     * <p>
     * Переопределен для корректной работы с ленивой загрузкой Hibernate.
     * Два объекта Comment считаются равными, если они имеют одинаковый
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
        Comment comment = (Comment) object;
        return getId() != null && Objects.equals(getId(), comment.getId());
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
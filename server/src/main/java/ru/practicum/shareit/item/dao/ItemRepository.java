package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId);

    @Query("""
            SELECT i FROM Item i
            WHERE i.available = true
            AND (
                lower(i.name) LIKE lower(concat('%', :text, '%'))
                OR lower(i.description) LIKE lower(concat('%', :text, '%'))
            )
            """)
    List<Item> search(@Param("text") String text);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequest_Requestor_Id(Long requestorId);

    List<Item> findAllByRequestNotNull();
}

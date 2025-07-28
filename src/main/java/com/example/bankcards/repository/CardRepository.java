package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.util.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByOwnerId(Long ownerId);

    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<Card> findByNumber(String number);

    @Modifying
    @Query("UPDATE Card c SET c.status = :newStatus WHERE c.id = :id")
    int changeCardStatus(@Param("id") Long id, @Param("newStatus") CardStatus newStatus);

    @Modifying
    @Query("UPDATE Card c SET c.status = 'EXPIRED' WHERE c.expirationDate = :expirationDate")
    int updateExpiredCards(@Param("expirationDate") LocalDate expirationDate);

    @Modifying
    @Query(value = "UPDATE Cards SET owner_id = NULL WHERE id IN :cardIds", nativeQuery = true)
    void clearOwnerByCardIdIn(@Param("cardIds") List<Long> cardIds);

    @Modifying
    @Query(value = "DELETE FROM cards WHERE id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") Long id);
}

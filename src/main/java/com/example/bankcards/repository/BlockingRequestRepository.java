package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockingRequestRepository extends JpaRepository<CardBlockingRequest, Long> {

    List<CardBlockingRequest> findByCardId(Long cardId);

    @Modifying
    @Query("UPDATE CardBlockingRequest cbr SET cbr.status = 'COMPLETED' WHERE cbr.id IN :requestIds")
    int setCompletedStatusByRequestIds(@Param("requestIds") List<Long> requestIds);

    @Modifying
    @Query(value = "UPDATE blocking_requests SET card_id = NULL WHERE card_id = :id", nativeQuery = true)
    void clearCardsByCardId(@Param("id") Long id);
}

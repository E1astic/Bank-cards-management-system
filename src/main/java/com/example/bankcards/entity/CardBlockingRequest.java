package com.example.bankcards.entity;

import com.example.bankcards.util.enums.BlockingRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocking_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBlockingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BlockingRequestStatus status;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}

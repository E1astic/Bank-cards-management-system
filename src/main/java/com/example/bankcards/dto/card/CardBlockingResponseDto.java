package com.example.bankcards.dto.card;

import com.example.bankcards.util.enums.BlockingRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBlockingResponseDto {

    private Long cardId;

    private BlockingRequestStatus status;

    private LocalDateTime timestamp;
}

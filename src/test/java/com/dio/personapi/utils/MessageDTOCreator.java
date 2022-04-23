package com.dio.personapi.utils;

import com.dio.personapi.dto.MessageResponseDTO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageDTOCreator {
    public static MessageResponseDTO messageResponseCreator() {
        return MessageResponseDTO.builder()
                .message("Saved person with Id 1")
                .build();
    }
}

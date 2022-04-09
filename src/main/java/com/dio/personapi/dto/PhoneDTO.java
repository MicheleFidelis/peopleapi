package com.dio.personapi.dto;

import com.dio.personapi.enums.PhoneType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDTO {
    Long id;

    @Enumerated
    PhoneType type;

    @NotEmpty
    @Size(min = 13, max = 14)
    String number;
}

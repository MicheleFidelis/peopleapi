package com.dio.personapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private Long id;

    @NotEmpty(message = "The fisrt name cannot be empty")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotEmpty(message = "The last name cannot be empty")
    @Size(min = 2, max = 100)
    private String lastName;

    @NotEmpty(message = "The CPF cannot be empty")
    @CPF
    private String cpf;

    @NotNull(message = "The birthdate cannot be null")
    private String birthDate;

    @Valid
    @NotEmpty(message = "The phone cannot be empty")
    private List<PhoneDTO> phones;
}

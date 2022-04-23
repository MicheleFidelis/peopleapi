package com.dio.personapi.utils;

import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;

@Builder
@Getter
public class PersonUtils {

    public static final LocalDate BIRTH_DATE = LocalDate.of(2010, 10, 1);
    private static final String FIRST_NAME = "Michele";
    private static final String LAST_NAME = "Fidelis";
    private static final String CPF_NUMBER = "369.333.878-79";
    private static final long PERSON_ID = 1L;

    public static PersonDTO createFakeDTO() {
        return PersonDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .cpf(CPF_NUMBER)
                .birthDate("04-04-2010")
                .phones(Collections.singletonList(PhoneUtils.createFakeDTO()))
                .build();
    }

    public static PersonDTO createFakeDTOEntity() {
        return PersonDTO.builder()
                .id(1L)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .cpf(CPF_NUMBER)
                .birthDate("04-04-2010")
                .phones(Collections.singletonList(PhoneUtils.createFakeDTO()))
                .build();
    }

    public static Person createFakeEntity() {
        return Person.builder()
                .id(PERSON_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .cpf(CPF_NUMBER)
                .birthDate(BIRTH_DATE)
                .phones(Collections.singletonList(PhoneUtils.createFakeEntity()))
                .build();
    }

    public static Person createFakePerson() {
        return Person.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .cpf(CPF_NUMBER)
                .birthDate(BIRTH_DATE)
                .phones(Collections.singletonList(PhoneUtils.createFakePhone()))
                .build();
    }
}

package com.dio.personapi.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ValidationExceptionsDetails extends ExceptionDetails{
    private final String fields;
    private final String fieldsMessage;
}

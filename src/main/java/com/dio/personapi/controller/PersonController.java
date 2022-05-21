package com.dio.personapi.controller;

import com.dio.personapi.dto.MessageResponseDTO;
import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import com.dio.personapi.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PersonController {

    private final PersonService personService;

    @Operation(summary = "Create a new person")
    @PostMapping
    public ResponseEntity<MessageResponseDTO> createPerson(@RequestBody @Valid PersonDTO personDTO) {
        return new ResponseEntity<>(personService.createPerson(personDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "List all people")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),})
    @GetMapping
    public ResponseEntity<Page<PersonDTO>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(personService.listAll(pageable));
    }

    @Operation(summary = "Get a person by id")
    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> findById(@Parameter(description = "id of person to be searched") @PathVariable Long id) {
        return ResponseEntity.ok(personService.findById(id));
    }

    @Operation(summary = "Get a person by id. Authentication is required")
    @GetMapping("/by-id/{id}")
    public ResponseEntity<PersonDTO> findByIdAuthenticationPrincipal(@Parameter(description = "id of person to be searched after authentication") @PathVariable Long id,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        log.info(String.valueOf(userDetails));
        return ResponseEntity.ok(personService.findById(id));
    }

    @Operation(summary = "Get a person by first name")
    @GetMapping("/find")
    public ResponseEntity<List<Person>> findByFirstName(@Parameter(description = "First name of person to be searched") @RequestParam String firstName) {
        return ResponseEntity.ok(personService.findByFirstName(firstName));
    }

    @Operation(summary = "Replace a person")
    @PutMapping("admin/{id}")
    public ResponseEntity<Void> replace(@Parameter(description = "id of person to be searched") @PathVariable Long id, @RequestBody PersonDTO personDTO) {
        personService.replace(id, personDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete a person by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Person delete",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Person not found",
                    content = @Content) })
    @DeleteMapping("admin/{id}")
    public ResponseEntity<Void> deleteById(@Parameter(description = "id of person to be searched") @PathVariable Long id) {
        personService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

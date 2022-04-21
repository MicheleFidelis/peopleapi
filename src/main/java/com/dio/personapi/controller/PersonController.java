package com.dio.personapi.controller;

import com.dio.personapi.dto.MessageResponseDTO;
import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import com.dio.personapi.exception.ResourceNotFoundException;
import com.dio.personapi.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createPerson(@RequestBody @Valid PersonDTO personDTO) {
        return new ResponseEntity<>(personService.createPerson(personDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> listAll() {
        return ResponseEntity.ok(personService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> findById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(personService.findById(id));
    }

    @GetMapping("/find")
    public ResponseEntity<List<Person>> findByFirstName(@RequestParam String firstName){
        return ResponseEntity.ok(personService.findByFirstName(firstName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> replace (@PathVariable Long id, @RequestBody PersonDTO personDTO) throws ResourceNotFoundException {
        personService.replace(id, personDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) throws ResourceNotFoundException {
        personService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.dio.personapi.service;

import com.dio.personapi.dto.MessageResponseDTO;
import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import com.dio.personapi.exception.ResourceNotFoundException;
import com.dio.personapi.mapper.PersonMapper;
import com.dio.personapi.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public MessageResponseDTO createPerson(PersonDTO personDTO) {
        Person personToSave = personMapper.toModel(personDTO);

        Person savedPerson = personRepository.save(personToSave);
        return createMessageResponse(savedPerson.getId(), "Saved person with Id ");
    }

    public PersonDTO findById(Long id) throws ResourceNotFoundException {
        Person person = verifyIfExists(id);
        return personMapper.toDTO(person);
    }

    public Page<PersonDTO> listAll(Pageable pageable) {
        Page<Person> allPeople = personRepository.findAll(pageable);
        return allPeople.map(personMapper::toDTO);
    }

    public List<Person> findByFirstName(String name) {
        return personRepository.findByFirstName(name);
    }

    public void delete(Long id) throws ResourceNotFoundException {
        verifyIfExists(id);

        personRepository.deleteById(id);
    }

    public void replace(Long id, PersonDTO personDTO) throws ResourceNotFoundException {
        verifyIfExists(id);

        Person personToUpdate = personMapper.toModel(personDTO);

        Person savedPerson = personRepository.save(personToUpdate);

        createMessageResponse(savedPerson.getId(), "Update person with Id ");
    }

    private MessageResponseDTO createMessageResponse(Long id, String message) {
        return MessageResponseDTO
                .builder()
                .message(String.format("%s %d",message,id))
                .build();
    }

    private Person verifyIfExists(Long id) throws ResourceNotFoundException {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }
}

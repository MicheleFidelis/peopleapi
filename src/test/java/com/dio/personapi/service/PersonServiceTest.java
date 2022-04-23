package com.dio.personapi.service;

import com.dio.personapi.dto.MessageResponseDTO;
import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import com.dio.personapi.exception.ResourceNotFoundException;
import com.dio.personapi.mapper.PersonMapper;
import com.dio.personapi.repository.PersonRepository;
import com.dio.personapi.utils.PersonUtils;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Test for class personService")
@Log4j2
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setup() throws ResourceNotFoundException {

        Person person = PersonUtils.createFakeEntity();
        PersonDTO personDTO = PersonUtils.createFakeDTO();

        List<Person> people = List.of(PersonUtils.createFakeEntity());

        BDDMockito.when(personMapper.toModel(personDTO))
                .thenReturn(person);

        BDDMockito.when(personMapper.toDTO(person))
                .thenReturn(personDTO);

        BDDMockito.when(personRepository.save(ArgumentMatchers.any(Person.class)))
                .thenReturn(person);

        BDDMockito.when(personRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(person));

        BDDMockito.when(personRepository.findByFirstName(ArgumentMatchers.anyString()))
                .thenReturn(people);

        BDDMockito.doNothing().when(personRepository).delete(ArgumentMatchers.any(Person.class));
    }

    @Test
    @DisplayName("CreatePerson returns success message when successful")
    void createPerson_ReturnsSuccessMessage_WhenSuccessful() {
        String expectedMessage = "Saved person with Id 1";

        MessageResponseDTO message = personService.createPerson(PersonUtils.createFakeDTO());

        assertThat(message).isNotNull();
        assertThat(message.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("ListAll returns list of people inside page object when successful")
    void listAll_ReturnsListOfPeopleInsidePageObject_WhenSuccessful() {
        PageImpl<Person> personPage = new PageImpl<Person>(List.of(PersonUtils.createFakeEntity()));

        BDDMockito.when(personMapper.toDTO(PersonUtils.createFakeEntity()))
                .thenReturn(PersonUtils.createFakeDTOEntity());
        BDDMockito.when(personRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(personPage);

        String expectedNameFirstName = PersonUtils.createFakeDTOEntity().getFirstName();
        log.info("test " + personPage.toList().size());

        Page<PersonDTO> personDTOPage = personService.listAll(PageRequest.of(1,3));

        assertThat(personDTOPage).isNotNull();
        assertThat(personDTOPage.toList().get(0).getFirstName()).isEqualTo(expectedNameFirstName);
    }

    @Test
    @DisplayName("FindById returns person when successful")
    void findById_ReturnsPerson_WhenSuccessful() {

        Long expectedId = PersonUtils.createFakeDTO().getId();

        PersonDTO personDTO = personService.findById(1L);

        assertThat(personDTO).isNotNull();
        assertThat(personDTO.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("FindById throws personNotFoundException when person is not found")
    void findById_ThrowsPersonNotFoundException_WhenPersonNotFound() {

        BDDMockito.when(personRepository.findById(ArgumentMatchers.anyLong()))
                .thenThrow(ResourceNotFoundException.class);

        Exception exception = null;

        try {
            PersonDTO personDTO = personService.findById(1L);
        } catch (ResourceNotFoundException e) {
            exception = e;
        }
        assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("FindById returns exception when id not found")
    void findById_ReturnsException_WhenIdNotFound() {

        BDDMockito.when(personService.findById(ArgumentMatchers.anyLong()))
                .thenThrow(ResourceNotFoundException.class);

        Exception exception = null;

        try {
            PersonDTO personDTO = personService.findById(1L);
        } catch (ResourceNotFoundException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("FindByFirstName returns list of people when successful")
    void findByFirstName_ReturnsListPeople_WhenSuccessful() {

        String expectedFirstName = PersonUtils.createFakeEntity().getFirstName();

        List<Person> person = personService.findByFirstName("Maria");

        assertThat(person).isNotNull();
        assertThat(person.get(0).getFirstName()).isEqualTo(expectedFirstName);
    }

    @Test
    @DisplayName("Replace update when successful")
    void replace_UpdatePerson_WhenSuccessful() {

        PersonDTO personDTO = PersonUtils.createFakeDTO();
        personDTO.setId(1L);

        Assertions.assertThatCode(() -> personService.replace(personDTO.getId(), personDTO))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Delete removes person when successful")
    void delete_RemovesPerson_WhenSuccessful() {
        Assertions.assertThatCode(() -> personService.delete(1L))
                .doesNotThrowAnyException();
    }
}
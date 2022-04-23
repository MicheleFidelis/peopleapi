package com.dio.personapi.controller;

import com.dio.personapi.dto.MessageResponseDTO;
import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import com.dio.personapi.exception.ResourceNotFoundException;
import com.dio.personapi.service.PersonService;
import com.dio.personapi.utils.MessageDTOCreator;
import com.dio.personapi.utils.PersonUtils;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Test for class personRepository")
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    @BeforeEach
    void setup() throws ResourceNotFoundException {

        PersonDTO personDTO = PersonUtils.createFakeDTO();
        MessageResponseDTO messageResponseDTO = MessageDTOCreator.messageResponseCreator();
        List<Person> people = List.of(PersonUtils.createFakeEntity());

        PageImpl<PersonDTO> personDTOPage = new PageImpl<PersonDTO>(List.of(PersonUtils.createFakeDTO()));

        BDDMockito.when(personService.createPerson(ArgumentMatchers.any(PersonDTO.class)))
                .thenReturn(messageResponseDTO);

        BDDMockito.when(personService.listAll(ArgumentMatchers.any()))
                .thenReturn(personDTOPage);

        BDDMockito.when(personService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(personDTO);

        BDDMockito.when(personService.findByFirstName(ArgumentMatchers.anyString()))
                .thenReturn(people);

        BDDMockito.doNothing().when(personService).replace(ArgumentMatchers.anyLong(), ArgumentMatchers.any(PersonDTO.class));

        BDDMockito.doNothing().when(personService).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("CreatePerson returns success message when successful")
    void createPerson_ReturnsSuccessMessage_WhenSuccessful() {
        String expectedMessage = "Saved person with Id 1";

        MessageResponseDTO message = personController.createPerson(PersonUtils.createFakeDTO()).getBody();

        assertThat(message).isNotNull();
        assertThat(message.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("List returns list of people inside page object when successful")
    void listAll_ReturnsListOfPeopleInsidePageObject_WhenSuccessful() {
        String expectedNameFirstName = PersonUtils.createFakeDTO().getFirstName();
        Page<PersonDTO> personDTOPage = personController.listAll(null).getBody();

        assertThat(personDTOPage).isNotNull();
        assertThat(personDTOPage.toList().get(0).getFirstName()).isEqualTo(expectedNameFirstName);
    }

    @Test
    @DisplayName("FindById returns person when successful")
    void findById_ReturnsPerson_WhenSuccessful() {

        Long expectedId = PersonUtils.createFakeDTO().getId();

        PersonDTO personDTO = personController.findById(1L).getBody();

        assertThat(personDTO).isNotNull();
        assertThat(personDTO.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("FindById returns exception when id not found")
    void findById_ReturnsException_WhenIdNotFound() {

        BDDMockito.when(personService.findById(ArgumentMatchers.anyLong()))
                .thenThrow(ResourceNotFoundException.class);

        Exception exception = null;

        try {
            PersonDTO personDTO = personController.findById(1L).getBody();
        } catch (ResourceNotFoundException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("FindByFirstName returns list of people when successful")
    void findByFirstName_ReturnsListPeople_WhenSuccessful() {

        String expectedFirstName = PersonUtils.createFakeEntity().getFirstName();

        List<Person> person = personController.findByFirstName("Maria").getBody();

        assertThat(person).isNotNull();
        assertThat(person.get(0).getFirstName()).isEqualTo(expectedFirstName);
    }

    @Test
    @DisplayName("Replace update when successful")
    void replace_UpdatePerson_WhenSuccessful() {

        PersonDTO personDTO = PersonUtils.createFakeDTO();

        Assertions.assertThatCode(() -> personController.replace(personDTO.getId(), personDTO))
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = personController.replace(personDTO.getId(), personDTO);

        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("DeleteById removes person when successful")
    void deleteById_RemovesPerson_WhenSuccessful() {

        PersonDTO personDTO = PersonUtils.createFakeDTO();

        Assertions.assertThatCode(() -> personController.deleteById(personDTO.getId()).getBody())
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = personController.deleteById(personDTO.getId());

        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
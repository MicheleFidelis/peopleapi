package com.dio.personapi.integration;

import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import com.dio.personapi.exception.ResourceNotFoundException;
import com.dio.personapi.repository.PersonRepository;
import com.dio.personapi.utils.PersonUtils;
import com.dio.personapi.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PersonControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PersonRepository personRepository;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("CreatePerson returns success message when successful")
    void createPerson_ReturnsSuccessMessage_WhenSuccessful() {
        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        Person person = testRestTemplate.postForObject("/api/v1/people", personSaved, Person.class);

        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(person.getId()).isNotNull();
        Assertions.assertThat(person.getId()).isEqualTo(personSaved.getId());
    }

    @Test
    @DisplayName("List returns list of people inside page object when successful")
    void listAll_ReturnsListOfPeopleInsidePageObject_WhenSuccessful() {

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        String expectedNameFirstName = personSaved.getFirstName();

        PageableResponse<PersonDTO> personDTOPage = testRestTemplate.exchange("/api/v1/people", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<PersonDTO>>() {
                }).getBody();

        Assertions.assertThat(personDTOPage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(personDTOPage).isNotNull();
        Assertions.assertThat(personDTOPage.toList().get(0).getFirstName()).isEqualTo(expectedNameFirstName);
    }

    @Test
    @DisplayName("FindById returns person when successful")
    void findById_ReturnsPerson_WhenSuccessful() {

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        Long expectedId = personSaved.getId();

        Person person = testRestTemplate.getForObject("/api/v1/people/{id}", Person.class, expectedId);

        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(person.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("FindById returns exception when id not found")
    void findById_ReturnsException_WhenIdNotFound() {

        Exception exception = null;

        try {
            Person person =  testRestTemplate.exchange("/api/v1/people/find?id=", HttpMethod.GET, null,
                    new ParameterizedTypeReference<Person>() {
                    }).getBody();
        } catch (ResourceNotFoundException e) {
            exception = e;
        }

        Assertions.assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("FindByFirstName returns list of people when successful")
    void findByFirstName_ReturnsListPeople_WhenSuccessful() {

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        String expectedFirstName = personSaved.getFirstName();

        String url = String.format("/api/v1/people/find?firstName=%s", expectedFirstName);
        List<Person> person =  testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Person>>() {
                }).getBody();

        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(person.get(0).getFirstName()).isEqualTo(expectedFirstName);
    }

    @Test
    @DisplayName("Replace update when successful")
    void replace_UpdatePerson_WhenSuccessful() {

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());
        personSaved.setLastName("New last name");

        ResponseEntity<Void> personResponseEntity = testRestTemplate.exchange("/api/v1/people/{id}", HttpMethod.PUT,
                new HttpEntity<>(personSaved), Void.class, personSaved.getId());

        Assertions.assertThat(personResponseEntity ).isNotNull();
        Assertions.assertThat(personResponseEntity .getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("DeleteById removes person when successful")
    void deleteById_RemovesPerson_WhenSuccessful() {

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        ResponseEntity<Void> personResponseEntity = testRestTemplate.exchange("/api/v1/people/{id}", HttpMethod.DELETE,
                null, Void.class, personSaved.getId());

        Assertions.assertThat(personResponseEntity).isNotNull();
        Assertions.assertThat(personResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}

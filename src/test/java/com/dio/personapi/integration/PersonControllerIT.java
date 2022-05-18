package com.dio.personapi.integration;

import com.dio.personapi.dto.PersonDTO;
import com.dio.personapi.entities.Person;
import com.dio.personapi.entities.PersonUser;
import com.dio.personapi.exception.ResourceNotFoundException;
import com.dio.personapi.repository.PersonRepository;
import com.dio.personapi.repository.PersonUserRepository;
import com.dio.personapi.utils.PersonUserCreater;
import com.dio.personapi.utils.PersonUtils;
import com.dio.personapi.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateAdmin;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonUserRepository personUserRepository;

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("userVisit", "projectSpringBoot");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("Michele", "projectSpringBoot");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

//    @Test
//    @DisplayName("CreatePerson returns success message when successful")
//    void createPerson_ReturnsSuccessMessage_WhenSuccessful() {
//
//        PersonUser user = PersonUserCreater.PersonUserUSER();
//        personUserRepository.save(user);
//
//        Person person = testRestTemplateUser.postForObject("/api/v1/people", personSaved, Person.class);
//
//        Assertions.assertThat(person).isNotNull();
//        Assertions.assertThat(person.getId()).isNotNull();
//        Assertions.assertThat(person.getId()).isEqualTo(personDTO.getId());
//    }

    @Test
    @DisplayName("List returns list of people inside page object when successful")
    void listAll_ReturnsListOfPeopleInsidePageObject_WhenSuccessful() {

        PersonUser user = PersonUserCreater.PersonUserUSER();
        personUserRepository.save(user);

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        String expectedNameFirstName = personSaved.getFirstName();

        PageableResponse<PersonDTO> personDTOPage = testRestTemplateUser.exchange("/api/v1/people", HttpMethod.GET, null,
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

        PersonUser user = PersonUserCreater.PersonUserUSER();
        personUserRepository.save(user);

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        Long expectedId = personSaved.getId();

        Person person = testRestTemplateUser.getForObject("/api/v1/people/{id}", Person.class, expectedId);

        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(person.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("FindById returns exception when id not found")
    void findById_ReturnsException_WhenIdNotFound() {

        PersonUser user = PersonUserCreater.PersonUserUSER();
        personUserRepository.save(user);

        Person person = PersonUtils.createFakeEntity();
        Exception exception = null;

        try {
            Person personFound = testRestTemplateUser.getForObject("/api/v1/people/{id}", Person.class, person.getId());

        } catch (ResourceNotFoundException e) {
            exception = e;
        }

        Assertions.assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("FindByFirstName returns list of people when successful")
    void findByFirstName_ReturnsListPeople_WhenSuccessful() {

        PersonUser user = PersonUserCreater.PersonUserUSER();
        personUserRepository.save(user);

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        String expectedFirstName = personSaved.getFirstName();

        String url = String.format("/api/v1/people/find?firstName=%s", expectedFirstName);
        List<Person> person =  testRestTemplateUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Person>>() {
                }).getBody();

        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(person.get(0).getFirstName()).isEqualTo(expectedFirstName);
    }

    @Test
    @DisplayName("Replace update when successful")
    void replace_UpdatePerson_WhenSuccessful() {

        PersonUser user = PersonUserCreater.PersonUserADMIN();
        personUserRepository.save(user);

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());
        personSaved.setLastName("New last name");

        ResponseEntity<Void> personResponseEntity = testRestTemplateAdmin.exchange("/api/v1/people/admin/{id}", HttpMethod.PUT,
                new HttpEntity<>(personSaved), Void.class, personSaved.getId());

        Assertions.assertThat(personResponseEntity ).isNotNull();
        Assertions.assertThat(personResponseEntity .getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("DeleteById removes person when successful")
    void deleteById_RemovesPerson_WhenSuccessful() {

        PersonUser user = PersonUserCreater.PersonUserADMIN();
        personUserRepository.save(user);

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        ResponseEntity<Void> personResponseEntity = testRestTemplateAdmin.exchange("/api/v1/people/admin/{id}", HttpMethod.DELETE,
                null, Void.class, personSaved.getId());

        Assertions.assertThat(personResponseEntity).isNotNull();
        Assertions.assertThat(personResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("DeleteById returns 403 when user is not admin")
    void deleteById_Returns403_WhenUserIsNotAdmin() {

        PersonUser user = PersonUserCreater.PersonUserUSER();
        personUserRepository.save(user);

        Person personSaved = personRepository.save(PersonUtils.createFakePerson());

        ResponseEntity<Void> personResponseEntity = testRestTemplateUser.exchange("/api/v1/people/admin/{id}", HttpMethod.DELETE,
                null, Void.class, personSaved.getId());

        Assertions.assertThat(personResponseEntity).isNotNull();
        Assertions.assertThat(personResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}

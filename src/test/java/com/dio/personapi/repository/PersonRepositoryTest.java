package com.dio.personapi.repository;

import com.dio.personapi.entities.Person;
import com.dio.personapi.entities.Phone;
import com.dio.personapi.enums.PhoneType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.dio.personapi.utils.PersonUtils.createFakePerson;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for person repository")
@Log4j2
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    private Person person;

    @BeforeEach
    public void setup() {
        person = createFakePerson();
    }

    @Test
    @DisplayName("JUnit test for save person operation")
    void givenPersonObject_whenSave_thenReturnSavedPerson() {

        Person savedPerson = this.personRepository.save(person);

        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getId()).isNotNull();
        assertThat(savedPerson.getFirstName()).isEqualTo(person.getFirstName());
        assertThat(savedPerson.getLastName()).isEqualTo(person.getLastName());
        assertThat(savedPerson.getCpf()).isEqualTo(person.getCpf());
        assertThat(savedPerson.getBirthDate()).isEqualTo(person.getBirthDate());
        assertThat(savedPerson.getCpf()).isEqualTo(person.getCpf());
        assertThat(savedPerson.getPhones()).isNotEmpty();

    }

    @Test
    @DisplayName("JUnit test for get all people operation")
    void givenPersonList_whenFindAll_thenReturnPersonList() {

        this.personRepository.save(person);

        Phone phone = Phone.builder()
                .type(PhoneType.HOME)
                .number("7777777777777")
                .build();

        Person person2 = Person.builder()
                .firstName("Ana")
                .lastName("Maria")
                .cpf("027.395.360-54")
                .birthDate(LocalDate.of(1290,12,14))
                .phones(Collections.singletonList(phone))
                .build();

        this.personRepository.save(person2);

        List<Person> peopleList = this.personRepository.findAll();

        assertThat(peopleList)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    @DisplayName("JUnit test for get people by id operation")
    void givenPersonObject_whenFindById_thenReturnPersonObject() {

        Person personSaved = this.personRepository.save(person);
        Person person = this.personRepository.findById(personSaved.getId()).get();
        assertThat(person).isNotNull();
    }

    @Test
    @DisplayName("JUnit test for replace people operation")
    void givenPersonObject_whenReplacePerson_thenReturnReplacePerson() {

        Person personSaved = this.personRepository.save(person);
        log.info(personSaved.getId());

        personSaved.setFirstName("Maria");
        personSaved.setLastName("Silva");

        Person personReplaced = this.personRepository.save(personSaved);

        assertThat(personReplaced).isNotNull();
        assertThat(personReplaced.getId()).isNotNull();
        assertThat(personReplaced.getFirstName()).isEqualTo("Maria");
        assertThat(personReplaced.getLastName()).isEqualTo("Silva");
    }

    @Test
    @DisplayName("JUnit test for delete people operation")
    void givenPersonObject_whenDeletePerson_thenRemovePerson() {

        Person personSaved = this.personRepository.save(person);

        this.personRepository.delete(personSaved);

        Optional<Person> personDelete = this.personRepository.findById (personSaved.getId());

        assertThat(personDelete).isEmpty();
    }

    @Test
    @DisplayName("JUnit test for find by first name people operation")
    void givenPersonObject_whenFindByNameFirstPerson_thenReturnPerson() {

        Person personSaved = this.personRepository.save(person);

        String firstName = personSaved.getFirstName();

        List<Person> personList = personRepository.findByFirstName(firstName);

        assertThat(personList)
                .isNotEmpty()
                .contains(personSaved);
    }

    @Test
    @DisplayName("JUnit test for find by first name people operation when returns empty list")
    void givenPersonObject_whenFindByNameFirstPerson_thenReturnEmptyList() {
        List<Person> personList = personRepository.findByFirstName("Test");

        assertThat(personList).isEmpty();
    }

}
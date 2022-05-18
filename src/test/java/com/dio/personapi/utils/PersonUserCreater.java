package com.dio.personapi.utils;

import com.dio.personapi.entities.PersonUser;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PersonUserCreater {
    public static PersonUser PersonUserADMIN() {
        return PersonUser.builder()
                .name("Michele Fidelis")
                .password("{bcrypt}$2a$10$/gocJAThUu21wVQe9xMQGe9JeBzW4bxgvZE8ftkncoKtHpQ5SSUUS")
                .username("Michele")
                .authorities("ROLE_USER,ROLE_ADMIN")
                .build();
    }

    public static PersonUser PersonUserUSER() {
        return PersonUser.builder()
                .name("Visit")
                .password("{bcrypt}$2a$10$/gocJAThUu21wVQe9xMQGe9JeBzW4bxgvZE8ftkncoKtHpQ5SSUUS")
                .username("userVisit")
                .authorities("ROLE_USER")
                .build();
    }
}

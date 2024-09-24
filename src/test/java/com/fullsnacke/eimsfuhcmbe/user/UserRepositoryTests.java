package com.fullsnacke.eimsfuhcmbe.user;

import com.fullsnacke.eimsfuhcmbe.entity.Role;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddSuccess(){
        Role role = new Role();
        role.setId(1);

        User user = new User();
        user.setFuId("SE120001");
        user.setEmail("TienCV@gamil.com");
        user.setFirstName("Tien");
        user.setLastName("Chau Vinh");
        user.setGender(true);
        user.setRole(role);
        user.setDepartment("SE");

        User saveUser = userRepository.save(user);

        Assertions.assertNotNull(saveUser);
    }

    @Test
    public void testFindByFuid(){
        User user = userRepository.findByFuId("FU001");
        Assertions.assertNotNull(user);
    }

}

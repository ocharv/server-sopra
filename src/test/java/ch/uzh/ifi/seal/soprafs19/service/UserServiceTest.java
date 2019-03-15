package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.LinkedList;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @After
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    //tests createUser() method in the UserService class
    public void createUser() {
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser1 = new User();
        testUser1.setName("testName");
        testUser1.setUsername("testUsername");
        testUser1.setPassword("testPassword");
        User createdUser = userService.createUser(testUser1);
        Assert.assertNotNull(createdUser.getCreationDate());
        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.OFFLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }
    @Test
    //tests authenticateUser() method in the UserService class
    public void authenticateUser() {
        User testUser2 = new User();
        testUser2.setUsername("rania");
        testUser2.setPassword("rania1");
        User tester =userService.createUser(testUser2);
        User authUser = userService.authenticateUser(tester);
        Assert.assertEquals(authUser.getStatus(),UserStatus.ONLINE);
        /*String s = userRepository.findByUsername(tester.getUsername()).getStatus().toString();
        Assert.assertEquals("ONLINE",s);*/
    }
    @Test
    //tests updateUser() method in the UserService class
    public void updateUser(){
        //User A
        User testUserA = new User();
        testUserA.setUsername("rania");
        testUserA.setPassword("rania1");
        userService.createUser(testUserA);
        //User B
        User testUserB = new User();
        testUserB.setUsername("bob");
        testUserB.setPassword("bob1");
        userService.createUser(testUserB);
        //Updated User A
        User updatedUserA = new User();
        updatedUserA.setId(testUserA.getId());
        updatedUserA.setUsername("newUsernameA");
        updatedUserA.setName("Rania");
        updatedUserA.setDateOfBirth("05/05/1996");
        userService.updateUser(updatedUserA);
        //Update User B
        User updatedUserB = new User();
        updatedUserB.setId(testUserB.getId());
        updatedUserB.setUsername(testUserB.getUsername());
        updatedUserB.setName("Bobby");
        updatedUserB.setDateOfBirth("03/03/1999");
        userService.updateUser(updatedUserB);
        //Assertions for testUserA
        Assert.assertEquals("newUsernameA", userRepository.getById(testUserA.getId()).getUsername());
        Assert.assertEquals("Rania", userRepository.getById(testUserA.getId()).getName());
        Assert.assertEquals("05/05/1996", userRepository.getById(testUserA.getId()).getDateOfBirth());
        //Assertions for testUserB
        Assert.assertEquals("bob", userRepository.getById(testUserB.getId()).getUsername());
        Assert.assertEquals("Bobby", userRepository.getById(testUserB.getId()).getName());
        Assert.assertEquals("03/03/1999", userRepository.getById(testUserB.getId()).getDateOfBirth());
    }
    @Test
    //tests getUserById() method in UserService class
    public void getUserById() {
        User testUser3 = new User();
        testUser3.setUsername("rania");
        testUser3.setPassword("rania!");
        User createdU = userService.createUser(testUser3);
        User u = userService.getUserById(createdU.getId());
        Assert.assertEquals(createdU, u);
    }
    @Test
    //tests getUsers() method in UserService class
    public void getUsers(){
        //testUser4A
        User testUser4A = new User();
        testUser4A.setUsername("rania");
        testUser4A.setPassword("rania!");
        User createdUA = userService.createUser(testUser4A);
        //testUser4B
        User testUser4B = new User();
        testUser4B.setUsername("bob");
        testUser4B.setPassword("bob!");
        User createdUb = userService.createUser(testUser4B);
        Iterable<User> testUsers = userService.getUsers();
        LinkedList<User> list = new LinkedList<>();
        for (User u:testUsers){
            list.add(u);
        }
        Assert.assertEquals(createdUA, list.get(0));
        Assert.assertEquals(createdUb, list.get(1));
    }
    @Test
    //tests resetStatus() method in the UserService class
    public void resetStatus(){
        User testUser5 = new User();
        testUser5.setUsername("rania");
        testUser5.setPassword("rania1");
        User tester =userService.createUser(testUser5);
        User authUser = userService.authenticateUser(tester);
        userService.resetStatus(authUser.getToken());
        Assert.assertEquals(userRepository.findByUsername(authUser.getUsername()).getStatus(),UserStatus.OFFLINE);
    }
    @Test
    //tests isAuthenticated() method in the UserService class
    public void isAuthenticated(){
        User testUser6 = new User();
        testUser6.setUsername("rania");
        testUser6.setPassword("rania!");
        User tester =userService.createUser(testUser6);
        String token =tester.getToken();
        Boolean authenticated = userService.isAuthenticated(token);
        Assert.assertTrue(authenticated);
    }

}

package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // retrieves all the registered users
    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }
    //finds user info for a user with the specified id
    public User getUserById(Long id) {
        return this.userRepository.getById(id);
    }
    //creates/registers users
    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        SimpleDateFormat day = new SimpleDateFormat("dd/MM/yyyy");
        String date = day.format(new Date());
        newUser.setCreationDate(date);
        newUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
    //the users personal details are updated
    public void updateUser(User user) {
        Long id = user.getId();
        User updatedUser = this.userRepository.getById(id);
        updatedUser.setDateOfBirth(user.getDateOfBirth());
        updatedUser.setUsername(user.getUsername());
        updatedUser.setName(user.getName());
        userRepository.save(updatedUser);
    }
    //during login the user's credentials are verified.
    //if the verification is successful the user is logged in
    public User authenticateUser(User user){
        String username = user.getUsername();
        String password = user.getPassword();
        User authUser= this.userRepository.findByUsername(username);
        if (username.equals(authUser.getUsername()) && password.equals(authUser.getPassword())){
            authUser.setStatus(UserStatus.ONLINE);
            return authUser;
        }else{
            return null;
        }
    }
    //when the user logs out the status is reset back to OFFLINE
    public void resetStatus(String token){
        User user = this.userRepository.findByToken(token);
        user.setStatus(UserStatus.OFFLINE);
    }
    //makes sure that the user is authenticated.
    //if so the user is allowed to access "restricted" pages
    public Boolean isAuthenticated(String token){
        if (this.userRepository.findByToken(token) == null){
            return false;
        }else {
            return true;
        }
    }
}

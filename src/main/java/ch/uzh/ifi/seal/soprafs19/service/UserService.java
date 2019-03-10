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

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(Long id) {
        return this.userRepository.getById(id);
    }
    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        SimpleDateFormat day = new SimpleDateFormat("dd/MM/yyyy");
        String date = day.format(new Date());
        newUser.setCreationDate(date);
        newUser.setStatus(UserStatus.ONLINE);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
    public User updateUser(User user) {
        Long id = user.getId();
        User updatedUser = this.userRepository.getById(id);
        updatedUser.setDateOfBirth(user.getDateOfBirth());
        updatedUser.setUsername(user.getUsername());
        updatedUser.setName(user.getName());
        userRepository.save(updatedUser);
        return updatedUser;
    }
    public User authenticateUser(User user){
        String username = user.getUsername();
        String password = user.getPassword();
        User authUser= this.userRepository.findByUsername(username);
        if (username.equals(authUser.getUsername()) && password.equals(authUser.getPassword())){
            return authUser;
        }else{
            return null;
        }
    }
}

package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    //returns all the registered users
    @GetMapping("/users")
    public ResponseEntity<Iterable<User>> all(@RequestHeader(value="Token")String token){
        if(this.service.isAuthenticated(token)) {
            return new ResponseEntity<>(service.getUsers(),HttpStatus.OK); //200
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //404
        }
    }

    //a user is created
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User regUser) {
        try {
            this.service.createUser(regUser);
            String location = "/users"+ regUser.getId();
            return new ResponseEntity<>(location, HttpStatus.CREATED); // 201
        }catch(Exception e){
            String reason = "A user with this username already exists";
            return new ResponseEntity<>(reason,HttpStatus.CONFLICT); // 409
        }
    }
    // user login
    @PostMapping("/login")
    public ResponseEntity<User> authenticateUser(@RequestBody User user) {
        try {
            User authUser = this.service.authenticateUser(user);
            if (authUser != null) {
                return new ResponseEntity<>(authUser, HttpStatus.OK); // 200
            }else{
                return new ResponseEntity<>(null,HttpStatus.NOT_FOUND); //404
            }
        }catch(Exception e){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND); //404
        }
    }
    //returns the profile details of the user with the specified id
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@RequestHeader(value = "Token")String token,@PathVariable Long id){
        User user=this.service.getUserById(id);
        if (token.equals(user.getToken())){
            if (user != null){
                return new ResponseEntity<>(user, HttpStatus.OK); //200
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); //404
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //404
        }
    }
    //updates the profile details of the user with the specified id
    @PutMapping("/users/{id}")
    //@CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> updateUser(@PathVariable Long id,@RequestHeader(value="Token") String token,@RequestBody User updatedUser){
        User user = this.service.getUserById(id);
        if(token.equals(user.getToken())){
            try {
                this.service.updateUser(updatedUser);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); //204
            }
            catch(Exception e){
                String reason = "The user you tried to update does not exist";
                return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND); //404
            }
        }else{
            String reason = "The user you tried to update does not exist";
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND); //404
        }
    }
    // the opposite of login
    @PutMapping("/logout")
    //@CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> resetStatus(@RequestBody String  token){
        try {
            this.service.resetStatus(token);
            String message= "Logout successful. See you soon!";
            return new ResponseEntity<>(message,HttpStatus.NO_CONTENT); //204
        }
        catch(Exception e){
            String reason = "An error occurred during the logout";
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND); //404
        }
    }
}

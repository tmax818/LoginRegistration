package tylermaxwell.loginregistration.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import tylermaxwell.loginregistration.models.LoginUser;
import tylermaxwell.loginregistration.models.User;
import tylermaxwell.loginregistration.repositories.UserRepository;

import javax.validation.Valid;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User newUser, BindingResult result) {

        Optional<User> potentialUser = userRepository.findByEmail(newUser.getEmail());

        // Reject if email is taken (present in database)
        if(potentialUser.isPresent()) {
            result.rejectValue("email", "Matches", "An account with that email already exists!");
        }

        // Reject if password doesn't match confirmation
        if(!newUser.getPassword().equals(newUser.getConfirm())) {
            result.rejectValue("confirm", "Matches", "The Confirm Password must match Password!");
        }

        // Return null if result has errors
        if(result.hasErrors()) {
            return null;
        }
        // hash password
        String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
        // set password
        newUser.setPassword(hashed);
        // save user to database
        return userRepository.save(newUser);
    }

    public User login(@Valid LoginUser newLogin, BindingResult result) {

        Optional<User> potentialUser = userRepository.findByEmail(newLogin.getEmail());

        // Find user in the DB by email
        // Reject if NOT present
        if(!potentialUser.isPresent()) {
            result.rejectValue("email", "Matches", "User not found!");
            return null;
        }

        // User exists, retrieve user from DB
        User user = potentialUser.get();

        // Reject if BCrypt password match fails
        if(!BCrypt.checkpw(newLogin.getPassword(), user.getPassword())) {
            result.rejectValue("password", "Matches", "Invalid Password!");
        }
        // Return null if result has errors
        if(result.hasErrors()) {
            return null;
        }
        // Otherwise, return the user object
        return user;
    }

    public Object findById(Long id) {
        Optional<User> potentialUser = userRepository.findById(id);
        if(potentialUser.isPresent()) {
            return potentialUser.get();
        }
        return null;
    }

}
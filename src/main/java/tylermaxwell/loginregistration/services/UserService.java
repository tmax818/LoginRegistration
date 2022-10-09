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

    public User register(User user, BindingResult result) {
        // Create optional user
        Optional<User> userToRegister = userRepository.findByEmail(user.getEmail());
        // Reject if email is taken (present in database)
        if(userToRegister.isPresent()){
            result.rejectValue("email", "Matches", "You have already registered");
        }
        // Reject if password doesn't match confirmation
        if(!user.getPassword().equals(user.getConfirm())){
            result.rejectValue("confirm", "Matches", "Passwords do not match");
        }
        // Return null if result has errors
        if(result.hasErrors()){
            return null;
        }
        // hash password
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        // set password
        user.setPassword(hashed);
        // save user to database
        return userRepository.save(user);
    }

    public User login(@Valid LoginUser loginUser, BindingResult result) {
        // Create potential user
        // Find user in the DB by email
        Optional<User> userToLogin = userRepository.findByEmail(loginUser.getEmail());
        // Reject if NOT present
        if(!userToLogin.isPresent()){
            result.rejectValue("email", "Matches", "User not Found");
        }
        // User exists if you get to this line, so retrieve user from DB
        User user = userToLogin.get();
        // Reject if BCrypt password match fails
        if(!BCrypt.checkpw(loginUser.getPassword(), user.getPassword())){
            result.rejectValue("password", "Matches", "Invalid Credentials");
        }
        // Return null if result has errors
        if(result.hasErrors()){
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
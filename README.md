# Login and Registration

Register before login
Display form to the user

```java
@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model) {
        // Bind empty User and LoginUser objects to capture form input
        model.addAttribute("newUser", new User());
        model.addAttribute("newLogin", new LoginUser());
        return "index.jsp";
    }
}
```

process user input

```java
@Controller
public class HomeController {
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("newUser") User newUser, BindingResult result, Model model, HttpSession session) {
        User user = userService.register(newUser, result);
        if (result.hasErrors()) {
            model.addAttribute("newLogin", new LoginUser());
            return "index.jsp";
        }
        session.setAttribute("userId", user.getId());
        return "redirect:/welcome";
    }
}
```

`register` method in service

```java
@Service
public class UserService {
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
}
```




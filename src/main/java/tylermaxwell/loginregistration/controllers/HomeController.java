package tylermaxwell.loginregistration.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import tylermaxwell.loginregistration.models.LoginUser;
import tylermaxwell.loginregistration.models.User;
import tylermaxwell.loginregistration.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    private final UserService userService;
    public HomeController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public String index(Model model){
        // Bind empty User and LoginUser objects to capture form input
        model.addAttribute("reg", new User());
        model.addAttribute("login", new LoginUser());
        return "index.jsp";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("reg") User user, BindingResult result, Model model, HttpSession session){
        // register a new user via the service
        User userToRegister = userService.register(user, result);
        if(result.hasErrors()){
            model.addAttribute("login", new LoginUser());
            return "index.jsp";
        }
        session.setAttribute("userId", user.getId());
        return "redirect:/welcome";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("login") LoginUser loginUser, BindingResult result, Model model, HttpSession session) {
        // login a new user via the service
        User userToLogin = userService.login(loginUser, result);
        if(result.hasErrors() || userToLogin==null){
            model.addAttribute("reg", new User());
            return "index.jsp";
        }
        // add user id to session
        session.setAttribute("userId", userToLogin.getId());
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome(HttpSession session, Model model) {
        // If no userId is found, redirect to log out
        if(session.getAttribute("userId") == null){
            return "redirect:/logout";
        }
        // We get the userId from session and cast as Long
        Long userId = (Long) session.getAttribute("userId");
        // add user retrieved via the service to our model
        model.addAttribute("user", userService.findById(userId));
        return "welcome.jsp";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Set userId to null
        session.setAttribute("userId", null);
        // redirect to log in/register page
        return "redirect:/";
    }
}

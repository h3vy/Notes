package notes.controller;

import notes.entity.RegistrEntity;
import notes.entity.UserEntity;
import notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class RegistrationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("RegistrEntity",  new RegistrEntity());
        return "registration";
    }

    @PostMapping("/registration")
    public String sendRegistrationRequest(@ModelAttribute("registrEntity") RegistrEntity registrEntity, Model model) {
        UserEntity user = userRepository.findByUsername(registrEntity.getUsername());
        if (user == null) {
            userRepository.save(new UserEntity(registrEntity.getUsername(), bCryptPasswordEncoder.encode(registrEntity.getPassword())));
        } else {
            model.addAttribute("errorBlock", true);
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            model.addAttribute("RegistrEntity", new RegistrEntity ());
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/login";
    }


}

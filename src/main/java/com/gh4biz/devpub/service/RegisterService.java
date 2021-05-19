package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.RegErrors;
import com.gh4biz.devpub.model.RegisterResult;
import com.gh4biz.devpub.model.entity.CaptchaCode;
import com.gh4biz.devpub.model.entity.User;
import com.gh4biz.devpub.model.request.RegisterForm;
import com.gh4biz.devpub.model.response.ProfileEditResponse;
import com.gh4biz.devpub.repo.CaptchaRepository;
import com.gh4biz.devpub.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Service
public class RegisterService {
    private UserRepository userRepository;
    private CaptchaRepository captchaRepository;

    public RegisterService(UserRepository userRepository, CaptchaRepository captchaRepository) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
    }


    public ResponseEntity<RegisterResult> register(RegisterForm form) {
        CaptchaCode captcha = captchaRepository.findBySecretCode(form.getSecret());
        RegErrors regErrors = new RegErrors();

        if (!form.getCaptcha().equals(captcha.getCode())) {
            regErrors.setCaptcha("Код с картинки введён неверно");
            return ResponseEntity.ok(new RegisterResult(false, regErrors));
        }
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            regErrors.setEmail("Этот e-mail уже зарегистрирован");
            return ResponseEntity.ok(new RegisterResult(false, regErrors));
        }
        if (form.getName().isEmpty()) {
            regErrors.setName("Имя указано неверно");
            return ResponseEntity.ok(new RegisterResult(false, regErrors));
        }
        User user = new User(
                form.getName(),
                form.getEmail(),
                form.getPassword()
        );
        userRepository.save(user);

        return ResponseEntity.ok(new RegisterResult(true));
    }

    public ProfileEditResponse editProfile(String name,
                                           String email,
                                           String password,
                                           MultipartFile photo,
                                           Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        user.setName(name);
        user.setEmail(email);
        if (password != null)
            user.setPassword(
                    new BCryptPasswordEncoder().encode(password));
        userRepository.save(user);

        return new ProfileEditResponse(true);
    }
}

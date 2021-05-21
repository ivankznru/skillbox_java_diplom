package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.RegErrors;
import com.gh4biz.devpub.model.RegisterResult;
import com.gh4biz.devpub.model.entity.CaptchaCode;
import com.gh4biz.devpub.model.entity.User;
import com.gh4biz.devpub.model.request.ProfileEditForm;
import com.gh4biz.devpub.model.request.RegisterForm;
import com.gh4biz.devpub.model.response.ProfileEdit;
import com.gh4biz.devpub.repo.CaptchaRepository;
import com.gh4biz.devpub.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;

@Service
public class RegisterService {
    private UserRepository userRepository;
    private CaptchaRepository captchaRepository;

    @Value("${blogAvatarHeight}")
    private int blogAvatarHeight;

    @Value("${blogAvatarWidth}")
    private int blogAvatarWidth;

    @Value("${blogUploadImageSizeLimit}")
    private int blogUploadImageSizeLimit;

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

    public ProfileEdit editProfile(MultipartFile photo,
                                   ProfileEditForm form,
                                   Principal principal) {

        User user = userRepository.findByEmail(principal.getName()).get();
        HashMap<String, String> errors = new HashMap<>();
        Boolean emailChanged = !principal.getName().equals(form.getEmail());
        if (emailChanged &
                userRepository.findByEmail(form.getEmail()).isPresent()) {
            errors.put("email", "Этот e-mail уже зарегистрирован!");
            return new ProfileEdit(false, errors);
        }

        if (!form.getEmail().equals(principal.getName())){
            user.setEmail(form.getEmail());
        }

//        System.out.println("1");
        if (form.getName().length() < 3) {
            errors.put("name", "Имя указано неверно!");
            return new ProfileEdit(false, errors);
        }
//        System.out.println("2");
        user.setName(form.getName());
        //System.out.println("3");



        //principal.getName()
        //System.out.println("4");
        if (form.getPassword() != null)
            //System.out.println("5");
            user.setPassword(
                    new BCryptPasswordEncoder().encode(form.getPassword()));
        userRepository.save(user);
        return new ProfileEdit(true);
    }

    private RegisterResult checkEditForm(MultipartFile photo,
                                         Principal principal) {

        RegErrors regErrors = new RegErrors();
        //String principalEmail = principal.getName();

        //User user = userRepository.findByEmail(principal.getName()).get();

//        System.out.println(email);
//
//        if ((email != null)
//                && (userRepository.findByEmail(email).isPresent())) {
//            regErrors.setEmail("Этот e-mail уже зарегистрирован");
//            return new RegisterResult(false, regErrors);
//        }
//        if (photo.getSize() > blogUploadImageSizeLimit) {
//            regErrors.setEmail("Фото слишком большое, нужно не более " + blogUploadImageSizeLimit / 1048576 + "Mb");
//            return new RegisterResult(false, regErrors);
//        }

        return new RegisterResult(true);
    }

    private void saveAvatar(MultipartFile photo) {
        // проверим размер и если не такой то ресайзнем а затем сохраним
    }

    private void imageResize() {
        // сделаем изображение правильного размера
    }

}

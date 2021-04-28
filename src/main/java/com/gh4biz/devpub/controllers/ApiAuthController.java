package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.entity.CaptchaCode;
import com.gh4biz.devpub.model.entity.User;
import com.gh4biz.devpub.model.request.RegisterForm;
import com.gh4biz.devpub.model.response.CaptchaResponse;
import com.gh4biz.devpub.model.response.RegError;
import com.gh4biz.devpub.model.response.RegisterResult;
import com.gh4biz.devpub.repo.CaptchaRepository;
import com.gh4biz.devpub.repo.UserRepository;
import net.bytebuddy.utility.RandomString;
import nl.captcha.Captcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private CaptchaRepository captchaRepository;
    private UserRepository userRepository;

    @Value("${blog.captchatimeout}")
    private String captchaTimeout;

    public ApiAuthController(
            CaptchaRepository captchaRepository,
            UserRepository userRepository) {
        this.captchaRepository = captchaRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/auth/captcha")
    private ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {
        deleteOldCaptchas();
        return ResponseEntity.ok(generateCaptcha());
    }

    @PostMapping("/auth/register")
    private ResponseEntity<RegisterResult> registerUser(@RequestBody RegisterForm form) {
        CaptchaCode captcha = captchaRepository.findBySecretCode(form.getSecret());
        RegError regErrors = new RegError();
        if (!form.getCaptcha().equals(captcha.getCode())) {
            regErrors.setCaptcha("Код с картинки введён неверно");
            return ResponseEntity.ok(new RegisterResult(false, regErrors));
        }
        if (!(userRepository.findByEmail(form.getEmail()) == null)){
            regErrors.setEmail("Этот e-mail уже зарегистрирован");
            return ResponseEntity.ok(new RegisterResult(false, regErrors));
        }
        if (form.getName().isEmpty()){
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

    private void deleteOldCaptchas() {
        Iterable<CaptchaCode> captchaCodes = captchaRepository.findAll();
        Calendar cSchedStartCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long gmtTime = cSchedStartCal.getTime().getTime();
        //     long timezoneAlteredTime = gmtTime + TimeZone.getTimeZone("Europe/Moscow").getRawOffset();
        for (CaptchaCode captchaCode : captchaCodes) {
            long captchaCodeLong = captchaCode.getTime().getTime() / 1000;
            if ((gmtTime / 1000 - captchaCodeLong) > Long.parseLong(captchaTimeout)) {
                captchaRepository.delete(captchaCode);
            }
        }
    }

    private CaptchaResponse generateCaptcha() throws IOException {
        Captcha captcha = new Captcha.Builder(130, 50)
                .addText()
                .build();
        RandomString randomString = new RandomString(5);
        String secret = randomString.nextString();

        byte[] bytes = toByteArray(captcha.getImage(), "png");
        String bytesBase64 = Base64.getEncoder().encodeToString(bytes);
        String prefix = "data:image/png;base64, ";
        CaptchaCode captchaCode = new CaptchaCode();

        String code = captcha.getAnswer();
        captchaCode.setCode(code);
        captchaCode.setTime(new Date());
        captchaCode.setSecretCode(secret);
        captchaRepository.save(captchaCode);
        return new CaptchaResponse(secret, prefix.concat(bytesBase64));
    }

    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}

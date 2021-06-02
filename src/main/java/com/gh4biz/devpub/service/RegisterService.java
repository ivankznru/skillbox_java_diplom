package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.RegErrors;
import com.gh4biz.devpub.model.RegisterResult;
import com.gh4biz.devpub.model.entity.CaptchaCode;
import com.gh4biz.devpub.model.entity.User;
import com.gh4biz.devpub.model.request.ChangePasswordForm;
import com.gh4biz.devpub.model.request.ProfileEditForm;
import com.gh4biz.devpub.model.request.RegisterForm;
import com.gh4biz.devpub.model.response.ProfileEdit;
import com.gh4biz.devpub.model.response.Result;
import com.gh4biz.devpub.repo.CaptchaRepository;
import com.gh4biz.devpub.repo.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;
    private final AuthenticationManager authenticationManager;

    @Value("${blogImageDBPathFolder}")
    private String blogImageDBPathFolder;

    @Value("${blogImageRealPathFolder}")
    private String blogImageRealPathFolder;

    @Value("${blogAvatarHeight}")
    private int blogAvatarHeight;

    @Value("${blogAvatarWidth}")
    private int blogAvatarWidth;

    @Value("${blogEmailUser}")
    private String blogEmailUser;

    @Value("${blogEmailPassword}")
    private String blogEmailPassword;

    @Value("${blogUrl}")
    private String blogUrl;

    public RegisterService(UserRepository userRepository,
                           CaptchaRepository captchaRepository,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.authenticationManager = authenticationManager;
    }


    public ResponseEntity<RegisterResult> register(RegisterForm form) {
        RegErrors regErrors = new RegErrors();
        if (!checkCaptcha(form.getSecret())) {
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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        User user = new User(
                form.getName(),
                form.getEmail(),
                encoder.encode(form.getPassword())
        );
        userRepository.save(user);

        return ResponseEntity.ok(new RegisterResult(true));
    }

    private boolean checkCaptcha(String secret) {
        CaptchaCode captcha = captchaRepository.findBySecretCode(secret);
        if (!secret.equals(captcha.getSecretCode())) {
            return false;
        }
        return true;
    }

    public ProfileEdit editProfile(ProfileEditForm form,
                                   Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        if (form.getRemovePhoto() == 1) {
            String path = "src\\main" + user.getPhoto();
            File file = new File(path);
            System.out.println(path);
            if (file.delete()) {
                user.setPhoto("");
                userRepository.save(user);
            }
        }

        HashMap<String, String> errors = new HashMap<>();
        boolean emailChanged = !principal.getName().equals(form.getEmail());

        if (emailChanged &
                userRepository.findByEmail(form.getEmail()).isPresent()) {
            errors.put("email", "Этот e-mail уже зарегистрирован!");
            return new ProfileEdit(false, errors);
        }

        if (!form.getEmail().equals(principal.getName())) {
            user.setEmail(form.getEmail());

            userRepository.save(user);
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        if (form.getName().length() < 3) {
            errors.put("name", "Имя указано неверно!");
            return new ProfileEdit(false, errors);
        }

        user.setName(form.getName());

        if (form.getPassword() != null)
            user.setName(form.getName());

        if (form.getPassword() != null)
            user.setPassword(
                    new BCryptPasswordEncoder().
                            encode(form.getPassword()));
        userRepository.save(user);
        return new ProfileEdit(true);
    }

    public ProfileEdit editAvatar(MultipartFile photo, Principal principal) throws IOException {
        if (!photo.getContentType().equals("image/png") &&
                !photo.getContentType().equals("image/jpg") &&
                !photo.getContentType().equals("image/jpeg")
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "неподдерживаемый тип файла!");
        }

        User user = userRepository.findByEmail(principal.getName()).get();
        saveAvatar(photo, user);

        return new ProfileEdit(true);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int type) {
        Dimension newMaxSize = new Dimension(blogAvatarHeight, blogAvatarWidth);
        BufferedImage resizedImg = Scalr.resize(originalImage, Scalr.Method.QUALITY, newMaxSize.width, newMaxSize.height);
        return resizedImg;
    }

    private void saveAvatar(MultipartFile file, User user) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if ((bufferedImage.getHeight() > blogAvatarWidth) | (bufferedImage.getWidth() > blogAvatarHeight)) {
            bufferedImage = resizeImage(bufferedImage, BufferedImage.TYPE_INT_RGB);
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = generateKey(file.getOriginalFilename()) + "." + extension;
        String path = blogImageRealPathFolder + fileName;
        String dbPath = blogImageDBPathFolder + fileName;

        File outputFile = new File(path);
        ImageIO.write(bufferedImage, extension, outputFile);
        user.setPhoto(File.separator + dbPath);
        userRepository.save(user);
    }

    private String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8));
    }

    public Result restore(String email) {
        RandomString randomString = new RandomString(30);
        String code = randomString.nextString();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = userRepository.findByEmail(email).get();
            user.setCode(code);
            sendMail(email, code);
            userRepository.save(user);
            return new Result(true);
        }
        return new Result(false);
    }

    private void sendMail(String email, String code) {
        String from = blogEmailUser;
        String host = "smtp.beget.com";

        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(blogEmailUser, blogEmailPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Devpub: Восстановление пароля");
            message.setText(blogUrl + "/login/change-password/" + code);
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public RegisterResult changePassword(ChangePasswordForm changePasswordForm) {
        if (!checkCaptcha(changePasswordForm.getCaptchaSecret())){
            RegErrors regErrors = new RegErrors();
            regErrors.setCaptcha("Неверный код");
            return new RegisterResult(false, regErrors);
        }
        Optional<User> optionalUser = userRepository.findUserByCode(changePasswordForm.getCode());
        if (optionalUser.isEmpty()){
            return new RegisterResult(false);
        }
        User user = optionalUser.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(changePasswordForm.getPassword()));
        userRepository.save(user);
        return new RegisterResult(true);
    }
}

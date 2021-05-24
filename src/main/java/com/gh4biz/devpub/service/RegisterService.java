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
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;

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

    @Value("${blogUploadImageSizeLimit}")
    private int blogUploadImageSizeLimit;

    public RegisterService(UserRepository userRepository,
                           CaptchaRepository captchaRepository,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.authenticationManager = authenticationManager;
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

    public ProfileEdit editProfile(ProfileEditForm form,
                                   //ProfileEditForm photo,
                                   Principal principal) {


        User user = userRepository.findByEmail(principal.getName()).get();
//        if (photo.getPhoto() != null) {
//            saveAvatar(user, photo.getPhoto());
//        }

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
        BufferedImage resizedImage = new BufferedImage(blogAvatarWidth, blogAvatarHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, blogAvatarWidth, blogAvatarHeight, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;
    }

    private void saveAvatar(MultipartFile file, User user) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if ((bufferedImage.getHeight() > blogAvatarWidth) | (bufferedImage.getWidth() > blogAvatarHeight)) {
            bufferedImage = resizeImage(bufferedImage, BufferedImage.TYPE_INT_RGB);
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName =  generateKey(file.getOriginalFilename())+ "." + extension;
        String path = blogImageRealPathFolder + File.separator + fileName;
        String dbPath = blogImageDBPathFolder + File.separator + fileName;

        File outputFile = new File(path);
        ImageIO.write(bufferedImage, extension, outputFile);
        user.setPhoto(File.separator + dbPath);
        userRepository.save(user);
    }

    private String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8));
    }

}

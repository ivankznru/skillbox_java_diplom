package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.ModerationStatus;
import com.gh4biz.devpub.model.request.LoginRequest;
import com.gh4biz.devpub.model.request.RegisterForm;
import com.gh4biz.devpub.model.request.RestoreForm;
import com.gh4biz.devpub.model.request.ChangePasswordForm;
import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.model.RegisterResult;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.UserRepository;
import com.gh4biz.devpub.service.CaptchaService;
import com.gh4biz.devpub.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;


@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final CaptchaService captchaService;
    private final RegisterService registerService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final  PasswordEncoder passwordEncoder;


    @Autowired
    public ApiAuthController(
            CaptchaService captchaService,
            RegisterService registerService,
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.captchaService = captchaService;
        this.registerService = registerService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/auth/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {
        return ResponseEntity.ok(captchaService.generateCaptcha());
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResult> registerUser(@RequestBody RegisterForm form) {
        return registerService.register(form);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        if (userRepository.findByEmail(loginRequest.getEmail()).isEmpty()){
            return ResponseEntity.ok(new LoginResponse(false));
        }

        com.gh4biz.devpub.model.entity.User tryLoginUser = userRepository.findByEmail(loginRequest.getEmail()).get();

        if(!passwordEncoder.matches(loginRequest.getPassword(), tryLoginUser.getPassword())) {
            return ResponseEntity.ok(new LoginResponse(false));
        }
        
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(getLoginResponse(user.getUsername()));
    }

    @GetMapping("/auth/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(getLoginResponse(principal.getName()));
    }

    @GetMapping("/auth/logout")
    public String logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "index";
    }

    @PostMapping("/auth/restore")
    public ResponseEntity<Result> restore(@RequestBody RestoreForm restoreForm) {
        return ResponseEntity.ok(registerService.restore(restoreForm.getEmail()));
    }

    @PostMapping("/auth/password")
    public ResponseEntity<RegisterResult> passwordChange(@RequestBody ChangePasswordForm changePasswordForm) {
        return ResponseEntity.ok(registerService.changePassword(changePasswordForm));
    }

    private LoginResponse getLoginResponse(String email) {
        com.gh4biz.devpub.model.entity.User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setEmail(currentUser.getEmail());
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        userResponse.setId(currentUser.getId());
        userResponse.setName(currentUser.getName());
        userResponse.setPhoto(currentUser.getPhoto());
        if (currentUser.getIsModerator() == 1)
        userResponse.setModerationCount(postRepository.countByIsActiveAndStatusAndModerator(
                1,
                ModerationStatus.NEW,
                currentUser
        ));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userResponse);
        return loginResponse;
    }

}

package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.ProfileEditResponse;
import com.gh4biz.devpub.service.RegisterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiProfileController {
    RegisterService registerService;

    public ApiProfileController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping(path = "/profile/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileEditResponse> editProfile(
            @RequestPart(required = false) MultipartFile photo,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            Principal principal) {
        return ResponseEntity.ok(registerService.editProfile(name, email, password, photo, principal));
    }

}

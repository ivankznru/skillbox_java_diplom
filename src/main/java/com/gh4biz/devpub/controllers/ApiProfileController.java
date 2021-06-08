package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.request.ProfileEditForm;
import com.gh4biz.devpub.model.response.ProfileEdit;
import com.gh4biz.devpub.service.RegisterService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiProfileController {
    RegisterService registerService;

    public ApiProfileController(RegisterService registerService) {
        this.registerService = registerService;
    }


    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileEdit> editProfile(
            @RequestBody ProfileEditForm form,
            Principal principal) {
        return ResponseEntity.ok(registerService.editProfile(form, principal));
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileEdit> saveAva(
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Principal principal) throws IOException {
        return ResponseEntity.ok(registerService.editAvatar(photo, principal));
//        return ResponseEntity.ok(registerService.editProfile(photo, photo, principal));
    }

}


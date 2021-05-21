package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.request.ProfileEditForm;
import com.gh4biz.devpub.model.response.ProfileEdit;
import com.gh4biz.devpub.service.RegisterService;
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

    //@PostMapping(path = "/profile/my")

    @RequestMapping(value = "/profile/my", method = RequestMethod.POST, consumes = {"multipart/form-data", "application/json"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileEdit> editProfile(
            @RequestParam(required = false) MultipartFile photo,
            @RequestBody ProfileEditForm form,
            Principal principal) {

//        System.out.println(name);
//        System.out.println(email);
//        System.out.println(password);
//        System.out.println(photo);
//        System.out.println(form);
//        System.out.println(principal);

        return ResponseEntity.ok(registerService.editProfile(photo, form, principal));
    }

}


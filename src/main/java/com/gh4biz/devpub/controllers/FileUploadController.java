package com.gh4biz.devpub.controllers;

import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileUploadController {
    @RequestMapping(value = "/api/image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('user:write')")
    public String uploadFile(MultipartFile image) throws IOException {
        String save = saveUploadedFile(image);
        return save;
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        if (!file.getContentType().equals("image/png") &&
                !file.getContentType().equals("image/jpg") &&
                !file.getContentType().equals("image/jpeg")
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "неподдерживаемый тип файла!");
        }

        RandomString randomString = new RandomString(2);
        String currentDir = "upload";
        for (int i = 0; i < 3; i++) {
            currentDir += File.separator.concat(randomString.nextString());
            if (!Files.exists(Paths.get(currentDir))) {
                Files.createDirectory(Paths.get(currentDir));
            }
        }
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(currentDir + File.separator + file.getOriginalFilename());
            Files.write(path, bytes);
            return path.toString();
        }
        return "неизвестная ошибка";
    }
}

package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.Response;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileUploadController {
    @RequestMapping(value = "/api/image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> uploadFile(MultipartFile image) {
        try {
            Response response = saveUploadedFile(image);
            if (response.isResult())
                return new ResponseEntity<>(response, HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false), HttpStatus.BAD_REQUEST);
        }
    }

    private Response saveUploadedFile(MultipartFile file) throws IOException {
        if ((file.getContentType().equals("image/png")) ||
                (!file.getContentType().equals("image/jpeg")) ||
                (!file.getContentType().equals("image/jpg"))) {
            return new Response(false, "неподдерживаемый тип файла");
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
            return new Response(true, path.toString());
        }
        return new Response(false, "неизвестная ошибка");
    }
}

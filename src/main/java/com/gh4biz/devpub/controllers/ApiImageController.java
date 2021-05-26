package com.gh4biz.devpub.controllers;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RestController
public class ApiImageController {
    @Value("${blogImageRealPathFolder}")
    private String blogImageRealPathFolder;

    @Value("${blogImageDBPathFolder}")
    private String blogImageDBPathFolder;

    @RequestMapping(value = "/api/image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('user:write')")
    public String uploadFile(MultipartFile image) throws IOException {
        return saveUploadedFile(image);
    }

    @GetMapping("/resources/static/img/{f1}/{f2}/{f3}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(
            @PathVariable String f1,
            @PathVariable String f2,
            @PathVariable String f3,
            @PathVariable String filename) throws MalformedURLException {

        return ResponseEntity.ok(loadAsResource(
                blogImageRealPathFolder.concat("\\").
                        concat(f1).concat("\\").
                        concat(f2).concat("\\").
                        concat(f3).concat("\\").
                        concat(filename)));
    }

    @GetMapping("/resources/static/img/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getAvatar(
            @PathVariable String filename) throws MalformedURLException {
        return ResponseEntity.ok(loadAsResource(blogImageRealPathFolder + File.separator + filename));
    }

    public Resource loadAsResource(String filename) throws MalformedURLException {
        Path p = Paths.get(filename);
        Resource resource = new UrlResource(p.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            System.out.println("no file");
        }
        return null;
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
        String newImgDir = "";
        String newImgDirSite = "";

        for (int i = 0; i < 3; i++) {
            String tmp = randomString.nextString();
            newImgDir = newImgDir + File.separator + tmp;
            newImgDirSite = newImgDirSite + "/" + tmp;
            if (!Files.exists(Paths.get(blogImageRealPathFolder + newImgDir))) {
                Files.createDirectory(Paths.get(blogImageRealPathFolder + newImgDir));
            }
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            String fileName = generateKey(file.getOriginalFilename()) + "." + extension;

            Path path = Paths.get(blogImageRealPathFolder + newImgDir + File.separator + fileName);
            Files.write(path, bytes);
            return "/" + blogImageDBPathFolder + newImgDirSite + "/" + fileName;
        }
        return "неизвестная ошибка";
    }

    private String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8));
    }
}

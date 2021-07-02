package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.entity.Image;
import com.gh4biz.devpub.repo.ImageRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class ApiImageController {
    @Autowired
    ServletContext servletContext;

    private final ImageRepository imageRepository;

    public ApiImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @RequestMapping(value = "/api/image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('user:write')")
    public String uploadFile(MultipartFile image) throws IOException {
        return saveUploadedFile(image);
    }

    @RequestMapping(value = "/resources/static/img/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageAsResponseEntity2(@PathVariable String filename) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        byte[] image;
        if (filename.equals("icons-sprite.3d76bac4.svg")){

            InputStream in = new FileInputStream("src/main/resources/static/img/" + filename);
            image = IOUtils.toByteArray(in);
        } else {
            filename = "/resources/static/img/" + filename;
            Optional<Image> optionalImage = imageRepository.getImageByImageName(filename);
            if (optionalImage.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "картинка не найдена!");
            }
            image = optionalImage.get().getImage();
        }

        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(image, headers, HttpStatus.OK);
        return responseEntity;
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        if (!file.getContentType().equals("image/png") &&
                !file.getContentType().equals("image/jpg") &&
                !file.getContentType().equals("image/jpeg")
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "неподдерживаемый тип файла!");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            String fileName = "/resources/static/img/" + generateKey(file.getOriginalFilename()) + "." + extension;
//            String fileName = "/img/" + file.getOriginalFilename();
            Image image = new Image();
            image.setImage(bytes);
            image.setImageName(fileName);
            imageRepository.save(image);
            return fileName;
        }
        return "неизвестная ошибка";
    }

    private String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8));
    }
}

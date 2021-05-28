package com.gh4biz.devpub.controllers;

import net.bytebuddy.utility.RandomString;
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

@RestController
public class ApiImageController {
    @Value("${blogImageRealPathFolder}")
    private String blogImageRealPathFolder;

    @Value("${blogImageDBPathFolder}")
    private String blogImageDBPathFolder;

    @Autowired
    ServletContext servletContext;

    @RequestMapping(value = "/api/image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('user:write')")
    public String uploadFile(MultipartFile image) throws IOException {
        return saveUploadedFile(image);
    }

    @GetMapping("/resources/static/img/{f1}/{f2}/{f3}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(
            @PathVariable String f1,
            @PathVariable String f2,
            @PathVariable String f3,
            @PathVariable String filename) throws IOException {


        HttpHeaders headers = new HttpHeaders();
        String dir = blogImageRealPathFolder + 
                f1 + File.separator +
                f2 + File.separator +
                f3 + File.separator;

        InputStream in = new FileInputStream(dir + filename);
        byte[] media = IOUtils.toByteArray(in);
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/resources/static/img/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageAsResponseEntity(@PathVariable String filename) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        InputStream in = new FileInputStream(blogImageRealPathFolder + filename);
        byte[] media = IOUtils.toByteArray(in);
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
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

        RandomString randomString = new RandomString(2);
        String newBackImgDir = "";
        String newFrontImgDir = "";

        for (int i = 0; i < 3; i++) {
            String tmp = randomString.nextString();
            newBackImgDir = newBackImgDir + tmp + File.separator;
            newFrontImgDir = newFrontImgDir + tmp + File.separator;

            if (!Files.exists(Paths.get(blogImageRealPathFolder + newBackImgDir))) {
                Files.createDirectory(Paths.get(blogImageRealPathFolder + newBackImgDir));
            }
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            String fileName = generateKey(file.getOriginalFilename()) + "." + extension;

            Path path = Paths.get(blogImageRealPathFolder + newBackImgDir + fileName);
            Files.write(path, bytes);
            return File.separator + blogImageDBPathFolder + newFrontImgDir + fileName;
        }
        return "неизвестная ошибка";
    }

    private String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8));
    }
}

package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.Response;
import com.gh4biz.devpub.model.response.Result;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@ControllerAdvice
public class FileUploadExceptionAdvice {

    @Value("${blogUploadImageSizeLimit}")
    private int blogUploadImageSizeLimit;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response> handleMaxSizeException() {
        return ResponseEntity.badRequest().body(
                new Response(false,
                        "Размер файла превышает допустимый размер " +
                                (blogUploadImageSizeLimit / 1048576) + "Mb"));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<HttpStatus> accessDeniedExceptionHandler() {
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<HttpStatus> notFoundExceptionHandler() {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<HttpStatus> e() {
//        return new ResponseEntity<>(null, HttpStatus.e);
//    }

}
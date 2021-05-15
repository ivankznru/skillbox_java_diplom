package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@ControllerAdvice
public class FileUploadExceptionAdvice  {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response> handleMaxSizeException() {
        return ResponseEntity.badRequest().body(
                new Response(false, "Размер файла превышает допустимый размер"));
    }
}
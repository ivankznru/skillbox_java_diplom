package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.request.PostEditForm;
import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.service.PostService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;
//    @Value("${blogUploadImageSizeLimit}")
//    private String blogUploadImageSizeLimit;

    @Autowired
    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    public ResponseEntity<PostsResponse> listOfPosts(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "recent") String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/post/search")
    public ResponseEntity<PostsResponse> searchResponse(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String query
    ) {
        return ResponseEntity.ok(postService.getSearch(query, offset, limit));
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String date) {
        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostsResponse> getPostsByTag(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String tag) {
        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable int id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/post/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> listOfModerationPosts(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String status,
            Principal principal) {
        return postService.getModerationPosts(offset, limit, status, principal);
    }

    @GetMapping("/post/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> listOfMyPosts(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String status,
            Principal principal) {
        return postService.getMyPosts(offset, limit, status, principal);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostUpdateEditUploadErrorsResponse> postAdd(
            @RequestBody PostEditForm form,
            Principal principal) {
        return postService.postAddOrEditResult(0, form, principal);
    }

    @PutMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostUpdateEditUploadErrorsResponse> postEdit(
            @RequestBody PostEditForm form,
            @PathVariable int id,
            Principal principal) {
        return postService.postAddOrEditResult(id, form, principal);
    }

//    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('user:write')")
//    public ResponseEntity<String> addImage(
//            @RequestParam("image") MultipartFile imageFile,
//            Principal principal) throws IOException {
//
//
//        return ResponseEntity.ok(saveUploadedFile(imageFile));
//    }
//
//    private String saveUploadedFile(MultipartFile file) throws IOException {
//        if (file.getSize() > Long.parseLong(blogUploadImageSizeLimit)){
//            return "Размер файла превышает допустимый размер";
//        }
//        if ((!file.getContentType().equals("image/png"))
//                |(!file.getContentType().equals("image/jpg"))){
//            return "неподдерживаемый тип файла";
//        }
//
//        RandomString randomString = new RandomString(2);
//        String currentDir = "upload";
//        for (int i = 0; i < 3; i++) {
//            currentDir += File.separator.concat(randomString.nextString());
//            if (!Files.exists(Paths.get(currentDir))) {
//                Files.createDirectory(Paths.get(currentDir));
//            }
//        }
//        if (!file.isEmpty()) {
//            byte[] bytes = file.getBytes();
//            Path path = Paths.get(currentDir + File.separator + file.getOriginalFilename());
//            Files.write(path, bytes);
//            return path.toString();
//        }
//        return "error";
//    }
//
//    @Override
//    public ModelAndView resolveException(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Object object,
//            Exception exc) {
//
//        ModelAndView modelAndView = new ModelAndView("file");
//        if (exc instanceof MaxUploadSizeExceededException) {
//            modelAndView.getModel().put("message", "File size exceeds limit!");
//        }
//        return modelAndView;
//    }
}

package com.gh4biz.devpub.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    int id;
    long timestamp;
    @NonFinal
    Integer parentId;
    String text;
    UserWithPhotoResponse user;
}

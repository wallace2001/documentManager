package com.rest.documentManager.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {

    private String fileName;
    private String downloadURL;
    private String fileType;
    private long fileSize;
}

package com.rest.documentManager.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {

    private Long id;
    private String fileName;
    private String downloadURL;
    private String fileType;
    private long fileSize;
}

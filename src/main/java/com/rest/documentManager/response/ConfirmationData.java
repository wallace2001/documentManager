package com.rest.documentManager.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ConfirmationData {

    private LocalDateTime timeStamp;
    private Number status;
    private String error;
    private String title;

    public ConfirmationData(Number status, String error, String title, LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        this.status = status;
        this.error = error;
        this.title = title;
    }
}

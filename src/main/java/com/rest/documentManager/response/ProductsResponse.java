package com.rest.documentManager.response;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Category;
import com.rest.documentManager.entity.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductsResponse {

    private Long id;
    private Attachment attachment;
    private Category category;
    private Size size;
    private String name;
    private String description;
    private String slug;
    private BigDecimal price;
    private Boolean isUnique;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

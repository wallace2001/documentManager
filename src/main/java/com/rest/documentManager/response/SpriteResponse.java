package com.rest.documentManager.response;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Category;
import com.rest.documentManager.entity.Image;
import com.rest.documentManager.entity.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

public class SpriteResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private String slug;
    private Boolean isUnique;
    private Boolean active;
    private Category category;
    private Size size;
    private Image image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SpriteResponse(Long id, String name, BigDecimal price, String slug, Boolean isUnique, Boolean active, Category category, Size size, Image image, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.slug = slug;
        this.isUnique = isUnique;
        this.active = active;
        this.category = category;
        this.size = size;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

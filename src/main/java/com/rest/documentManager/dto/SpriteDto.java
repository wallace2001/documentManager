package com.rest.documentManager.dto;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Category;
import com.rest.documentManager.entity.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;

@Getter @Setter
@NoArgsConstructor
public class SpriteDto {
    @NotBlank
    private Integer attachment_id;
    @NotBlank
    private Long category_id;
    @NotBlank
    private Long size_id;
    @NotBlank
    private Long image_id;
    @NotBlank
    private Boolean active;
    @NotBlank
    private Boolean isUnique;
    @NotBlank
    private String slug;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private BigDecimal price;
}

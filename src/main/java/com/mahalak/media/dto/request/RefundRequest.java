package com.mahalak.media.dto.request;
import lombok.*; import jakarta.validation.constraints.NotBlank;
@Data @NoArgsConstructor @AllArgsConstructor public class RefundRequest { @NotBlank private String reason; }

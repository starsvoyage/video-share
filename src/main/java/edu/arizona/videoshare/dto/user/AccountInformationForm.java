package edu.arizona.videoshare.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AccountInformationForm {

    @NotBlank(message = "Display name is required.")
    @Size(max = 100, message = "Display name must be 100 characters or fewer.")
    private String displayName;

    @Size(max = 500, message = "Bio must be 500 characters or fewer.")
    private String bio;

    private MultipartFile avatar;
}
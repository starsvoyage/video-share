package edu.arizona.videoshare.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

    @NotBlank(message = "Username or email is required")
    @Size(max = 254, message = "Username or email is too long")
    private String identifier;

    @NotBlank(message = "Password is required")
    @Size(max = 72, message = "Password is too long")
    private String password;
}
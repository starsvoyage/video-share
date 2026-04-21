package edu.arizona.videoshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VideoShareApplication {

    public static void main(String[] args) {

        SpringApplication.run(VideoShareApplication.class, args);
    }

}

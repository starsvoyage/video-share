package edu.arizona.videoshare.config;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.model.enums.AdPlacement;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.repository.AdRepository;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import edu.arizona.videoshare.service.UserService;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * DataLoader
 *
 * Seeds initial data into the database when the application starts.
 *
 * Profile restriction:
 * Disabled when the "test" profile is active to prevent interference with
 * automated tests.
 */
@Profile("!test")
@Component
public class DataLoader implements CommandLineRunner {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final VideoRepository videoRepository;
    private final BCryptPasswordEncoder encoder;

    public DataLoader(UserService userService,
                      UserRepository userRepository,
                      ChannelRepository channelRepository,
                      SubscriptionRepository subscriptionRepository,
                      VideoRepository videoRepository,
                      BCryptPasswordEncoder encoder, AdRepository adRepository) {

        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.videoRepository = videoRepository;
        this.encoder = encoder;
        this.adRepository = adRepository;
    }

    /**
     * Runs automatically at application startup.
     * Seeds users only if the database is empty to prevent duplicate insertions.
     */
    @Override
    public void run(String... args) {
        // Avoid reseeding on restart
        if (userRepository.count() > 0)
            return;

        seed("starsvoyage", "idiazvachier@arizona.edu", "Password@123");
        seed("user1", "user1@ua.edu", "User1@123");

        User ian = userRepository.findByUsername("starsvoyage").orElse(null);
        User user1 = userRepository.findByUsername("user1").orElse(null);

        if (ian != null && user1 != null) {
            // Adding channels
            Channel channel1 = new Channel();
            channel1.setName("Ian with Bob");
            channel1.setDescription("Programming tutorials");
            channel1.setUser(ian);
            channelRepository.save(channel1);

            Channel channel2 = new Channel();
            channel2.setName("Gaming");
            channel2.setDescription("Gaming content");
            channel2.setUser(user1);
            channelRepository.save(channel2);

            //Adding videos
            Video video1 = new Video();
            video1.setTitle("Welcome Video");
            video1.setOwner(ian);
            video1.setChannel(channel1);
            video1.setVisibility(VideoVisibility.PUBLIC);
            video1.setDuration(120);
            videoRepository.save(video1);

            Video video2 = new Video();
            video2.setTitle("Gaming Highlights");
            video2.setOwner(user1);
            video2.setChannel(channel2);
            video2.setVisibility(VideoVisibility.PUBLIC);
            video2.setDuration(300);
            videoRepository.save(video2);

            // Adding subscriptions
            Subscription sub1 = new Subscription();
            sub1.setSubscriber(ian);
            sub1.setChannel(channel1);
            sub1.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(sub1);
            channel1.setSubscriberCount(4L);
            channelRepository.save(channel1);

            Subscription sub2 = new Subscription();
            sub2.setSubscriber(user1);
            sub2.setChannel(channel2);
            sub2.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(sub2);
            channel2.setSubscriberCount(10L);
            channelRepository.save(channel2);

            // Adding ads
            Ad ad1 = new Ad();
            ad1.setTitle("Buy Premium Now!");
            ad1.setMediaUrl("https://www.w3schools.com/html/movie.mp4");
            ad1.setDuration(15);
            ad1.setActive(true);
            ad1.setPlacement(AdPlacement.Pre_roll);
            ad1.setStartAt(LocalDateTime.now().minusDays(1));
            ad1.setEndAt(LocalDateTime.now().plusDays(30));
            adRepository.save(ad1);

            Ad ad2 = new Ad();
            ad2.setTitle("Check out our store!");
            ad2.setMediaUrl("https://www.w3schools.com/html/movie.mp4");
            ad2.setDuration(30);
            ad2.setActive(true);
            ad2.setPlacement(AdPlacement.Mid_roll);
            ad2.setStartAt(LocalDateTime.now().minusDays(1));
            ad2.setEndAt(LocalDateTime.now().plusDays(30));
            adRepository.save(ad2);

            Ad ad4 = new Ad();
            ad4.setTitle("Thanks for watching!");
            ad4.setMediaUrl("https://www.w3schools.com/html/movie.mp4");
            ad4.setDuration(15);
            ad4.setActive(true);
            ad4.setPlacement(AdPlacement.Post_roll);
            ad4.setStartAt(LocalDateTime.now().minusDays(1));
            ad4.setEndAt(LocalDateTime.now().plusDays(30));
            adRepository.save(ad4);

            Ad ad3 = new Ad();
            ad3.setTitle("Shop Now!");
            ad3.setMediaUrl("https://via.placeholder.com/728x90.png?text=Advertisement");
            ad3.setDuration(0);
            ad3.setActive(true);
            ad3.setPlacement(AdPlacement.banner);
            ad3.setStartAt(LocalDateTime.now().minusDays(1));
            ad3.setEndAt(LocalDateTime.now().plusDays(30));
            adRepository.save(ad3);
        }
    }

    /**
     * Helper method to seed a user via service layer.
     */
    private void seed(String username, String email, String password) {

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email.trim().toLowerCase());
        user.setDisplayName(username);
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.CREATOR);

        UserCredentials credentials = new UserCredentials();
        credentials.setPasswordHash(encoder.encode(password));

        user.attachCredentials(credentials);

        userRepository.save(user);
    }
}

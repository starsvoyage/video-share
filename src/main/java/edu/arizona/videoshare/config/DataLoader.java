package edu.arizona.videoshare.config;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import edu.arizona.videoshare.service.UserService;
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
                      BCryptPasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.videoRepository = videoRepository;
        this.encoder = encoder;
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

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

        seed("starsvoyage", "idiazvachier@arizona.edu", "Password@123", "Ian");
        seed("user1", "user1@arizona.edu", "User1@123", "User 1");
        seed("alice", "alice@arizona.edu", "Password@123", "Alice Jones");
        seed("bob", "bob@arizona.edu", "Password@123", "Bob Viani");
        seed("charlie", "charlie@arizona.edu", "Password@123", "Charlie Miller");


        User ian = userRepository.findByUsername("starsvoyage").orElse(null);
        User user1 = userRepository.findByUsername("user1").orElse(null);
        User alice = userRepository.findByUsername("alice").orElse(null);
        User bob = userRepository.findByUsername("bob").orElse(null);
        User charlie = userRepository.findByUsername("charlie").orElse(null);

        if (ian != null && user1 != null && alice != null && bob != null && charlie != null) {
            Channel channel1 = createChannel("Ian with Bob", "Programming tutorials", ian);
            Channel channel2 = createChannel("Gaming", "Gaming content", user1);
            Channel channel3 = createChannel("Alice Cooks", "Easy weeknight recipes and meal prep", alice);
            Channel channel4 = createChannel("Bob Builds", "DIY projects and woodworking", bob);
            Channel channel5 = createChannel("Charlie Explores", "Travel vlogs and city guides", charlie);
            Channel channel6 = createChannel("Alice Gaming", "Casual gaming and reviews", alice);

            //Ian's channel
            createVideo("Welcome Video", ian, channel1);

            //User1's channel
            createVideo("Gaming Highlights", user1, channel2);
            createVideo("Minecraft Survival Guide", user1, channel2);
            createVideo("Minecraft Redstone Tutorial", user1, channel2);

            //Alice Cooks videos
            createVideo("Pasta Recipe", alice, channel3);
            createVideo("Meal Prep", alice, channel3);
            createVideo("Chocolate Cake", alice, channel3);

            //Bob Builds videos
            createVideo("Building a Bookshelf", bob, channel4);
            createVideo("DIY  Desk", bob, channel4);

            //Charlie Explores videos
            createVideo("Exploring Tokyo", charlie, channel5);
            createVideo("In London", charlie, channel5);
            createVideo("Hiking Sabino Canyon", charlie, channel5);

            //Alice Gaming videos
            createVideo("Minecraft Build", alice, channel6);
            createVideo("Stardew Valley", alice, channel6);

            //Create subscriptions
            createSubscription(ian, channel1);
            createSubscription(user1, channel2);
            createSubscription(alice, channel1);
            createSubscription(alice, channel2);
            createSubscription(bob, channel3);
            createSubscription(bob, channel5);
            createSubscription(charlie, channel3);
            createSubscription(charlie, channel4);
        }
    }

    /**
     * Helper method to seed a user via service layer.
     */
    private void seed(String username, String email, String password, String displayName) {

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

    //Helper functions to create channels, videos, subscriptions
    private Channel createChannel(String name, String description, User owner) {
        Channel channel = new Channel();
        channel.setName(name);
        channel.setDescription(description);
        channel.setUser(owner);
        channel.setSubscriberCount(0L);
        return channelRepository.save(channel);
    }

    private Video createVideo(String title, User owner, Channel channel) {
        Video video = new Video();
        video.setTitle(title);
        video.setOwner(owner);
        video.setChannel(channel);
        video.setVisibility(VideoVisibility.PUBLIC);
        //DURATION SET TO 10 SINCE DUMMY VIDEO LINKED BELOW IS 10 SECONDS LONG
        video.setDuration(10);
        video.setMediaUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        return videoRepository.save(video);
    }

    private void createSubscription(User subscriber, Channel channel) {
        Subscription sub = new Subscription();
        sub.setSubscriber(subscriber);
        sub.setChannel(channel);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(sub);
        channel.setSubscriberCount(channel.getSubscriberCount() + 1);
        channelRepository.save(channel);
    }
}

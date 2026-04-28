package edu.arizona.videoshare.config;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.model.enums.AdPlacement;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.repository.AdRepository;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import edu.arizona.videoshare.repository.*;
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
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final BCryptPasswordEncoder encoder;

    public DataLoader(UserRepository userRepository,
                      ChannelRepository channelRepository,
                      SubscriptionRepository subscriptionRepository,
                      VideoRepository videoRepository,
                      AdRepository adRepository,
                      MembershipPlanRepository membershipPlanRepository,
                      UserMembershipRepository userMembershipRepository,
                      BCryptPasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.videoRepository = videoRepository;
        this.adRepository = adRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.userMembershipRepository = userMembershipRepository;
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


        seedMembershipPlans();

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

            // Adding videos
            Video video1 = new Video();
            video1.setTitle("Welcome Video");
            video1.setOwner(ian);
            video1.setChannel(channel1);
            video1.setVisibility(VideoVisibility.PUBLIC);
            video1.setDuration(120);
            videoRepository.save(video1);
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

            Subscription sub2 = new Subscription();
            sub2.setSubscriber(user1);
            sub2.setChannel(channel2);
            sub2.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(sub2);
            channel2.setSubscriberCount(10L);
            channelRepository.save(channel2);

            // Add membership history + current membership for starsvoyage
            seedPremiumMembershipHistoryForStarsVoyage(ian);

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
            ad3.setMediaUrl("https://picsum.photos/728/90");
            ad3.setDuration(0);
            ad3.setActive(true);
            ad3.setPlacement(AdPlacement.banner);
            ad3.setStartAt(LocalDateTime.now().minusDays(1));
            ad3.setEndAt(LocalDateTime.now().plusDays(30));
            adRepository.save(ad3);


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
     * Helper method to seed a user directly.
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

    /**
     * Seed the two membership plans used by the app.
     */
    private void seedMembershipPlans() {
        if (!membershipPlanRepository.existsByCode("FREE")) {
            MembershipPlan free = new MembershipPlan();
            free.setCode("FREE");
            free.setName("Free");
            free.setCost(0);
            free.setAdFree(false);
            free.setActive(true);
            free.setHd4KPlayback(false);
            membershipPlanRepository.save(free);
        }

        if (!membershipPlanRepository.existsByCode("PREMIUM")) {
            MembershipPlan premium = new MembershipPlan();
            premium.setCode("PREMIUM");
            premium.setName("Premium");
            premium.setCost(999);
            premium.setAdFree(true);
            premium.setActive(true);
            premium.setHd4KPlayback(true);
            membershipPlanRepository.save(premium);
        }
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

    /**
     * Seed one old membership record and one current active membership for starsvoyage.
     */
    private void seedPremiumMembershipHistoryForStarsVoyage(User user) {
        if (!userMembershipRepository.findByUserIdOrderByStartAtDesc(user.getId()).isEmpty()) {
            return;
        }

        MembershipPlan premiumPlan = membershipPlanRepository.findByCode("PREMIUM")
                .orElse(null);

        if (premiumPlan == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 5 → 4 months ago (CANCELED)
        UserMembership canceled = UserMembership.builder()
                .user(user)
                .membershipPlan(premiumPlan)
                .status(MembershipStatus.CANCELED)
                .startAt(now.minusMonths(5))
                .endAt(now.minusMonths(4))
                .autoRenew(false)
                .build();

        // 4 → 3 months ago (ACTIVE - paid month)
        UserMembership month1 = UserMembership.builder()
                .user(user)
                .membershipPlan(premiumPlan)
                .status(MembershipStatus.ACTIVE)
                .startAt(now.minusMonths(4))
                .endAt(now.minusMonths(3))
                .autoRenew(true)
                .build();

        // 3 → 2 months ago (ACTIVE - paid month)
        UserMembership month2 = UserMembership.builder()
                .user(user)
                .membershipPlan(premiumPlan)
                .status(MembershipStatus.ACTIVE)
                .startAt(now.minusMonths(3))
                .endAt(now.minusMonths(2))
                .autoRenew(true)
                .build();

        // 2 → 1 month ago (ACTIVE - paid month)
        UserMembership month3 = UserMembership.builder()
                .user(user)
                .membershipPlan(premiumPlan)
                .status(MembershipStatus.ACTIVE)
                .startAt(now.minusMonths(2))
                .endAt(now.minusMonths(1))
                .autoRenew(true)
                .build();

        // Current (ACTIVE)
        UserMembership current = UserMembership.builder()
                .user(user)
                .membershipPlan(premiumPlan)
                .status(MembershipStatus.ACTIVE)
                .startAt(now.minusMonths(1))
                .endAt(null)
                .autoRenew(true)
                .build();

        userMembershipRepository.save(canceled);
        userMembershipRepository.save(month1);
        userMembershipRepository.save(month2);
        userMembershipRepository.save(month3);
        userMembershipRepository.save(current);
    }
}
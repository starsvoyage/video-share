package edu.arizona.videoshare.config;

import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.repository.*;
import edu.arizona.videoshare.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

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
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final BCryptPasswordEncoder encoder;

    public DataLoader(UserService userService,
                      UserRepository userRepository,
                      ChannelRepository channelRepository,
                      SubscriptionRepository subscriptionRepository,
                      VideoRepository videoRepository,
                      MembershipPlanRepository membershipPlanRepository,
                      UserMembershipRepository userMembershipRepository,
                      BCryptPasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.videoRepository = videoRepository;
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
        seed("starsvoyage", "idiazvachier@arizona.edu", "Password@123");
        seed("user1", "user1@ua.edu", "User1@123");

        seedMembershipPlans();

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

            // Adding videos
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

            // Add membership history + current membership for starsvoyage
            seedPremiumMembershipHistoryForStarsVoyage(ian);
        }
    }

    /**
     * Helper method to seed a user directly.
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
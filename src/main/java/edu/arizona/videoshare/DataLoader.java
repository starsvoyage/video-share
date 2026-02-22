package edu.arizona.videoshare;

import edu.arizona.videoshare.model.Channel;
import edu.arizona.videoshare.model.Subscription;
import edu.arizona.videoshare.model.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.model.User;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadChannelAndSubscriptions(
            UserRepository userRepository,
            ChannelRepository channelRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        return args -> {

            //Creating Users
            User user1 = new User();
            user1.setUsername("alice");
            user1.setEmail("alice@test.com");
            user1.setDisplayName("Alice");
            userRepository.save(user1);

            User user2 = new User();
            user2.setUsername("bob");
            user2.setEmail("bob@test.com");
            user2.setDisplayName("Bob");
            userRepository.save(user2);

            //Adding channels
            Channel channel1 = new Channel();
            channel1.setName("Alice with Bob");
            channel1.setDescription("Programming tutorials");
            channel1.setUser(user1);
            channelRepository.save(channel1);

            Channel channel2 = new Channel();
            channel2.setName("Gaming");
            channel2.setDescription("Gaming content");
            channel2.setUser(user2);
            channelRepository.save(channel2);

            //Adding subscriptions to channel
            Subscription sub1 = new Subscription();
            sub1.setSubscriber(user2);
            sub1.setChannel(channel1);
            sub1.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(sub1);

            Subscription sub2 = new Subscription();
            sub2.setSubscriber(user1);
            sub2.setChannel(channel2);
            sub2.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(sub2);

        };
    }
}


package edu.arizona.videoshare;

import edu.arizona.videoshare.model.Channel;
import edu.arizona.videoshare.model.Subscription;
import edu.arizona.videoshare.model.User;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

            //Getting the users
            edu.arizona.videoshare.model.User user1 = userRepository.findAll().get(0);
            User user2 = userRepository.findAll().get(1);

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
            sub1.setCreatedAt(LocalDateTime.now());
            subscriptionRepository.save(sub1);

            Subscription sub2 = new Subscription();
            sub2.setSubscriber(user1);
            sub2.setChannel(channel2);
            sub2.setCreatedAt(LocalDateTime.now());
            subscriptionRepository.save(sub2);

        };
    }
}


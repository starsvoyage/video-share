package edu.arizona.videoshare;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.arizona.videoshare.model.Subscription;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;

@SpringBootTest
class SubscriptionJpaTest {
    
    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    void printSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        System.out.println("User's and their subscriptions: ");
        for (int i = 0; i < subscriptions.size(); i++) {
            Subscription subscription = subscriptions.get(i);
            System.out.println("User: " + subscription.getSubscriber().getName() + ", Subscription: " + subscription.getChannel().getName());
        }
    }



}

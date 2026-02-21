package edu.arizona.videoshare;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.arizona.videoshare.model.Channel;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;

@SpringBootTest
class ChannelJpaTest {
    
    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    void printChannels() {
        List<Channel> channels = channelRepository.findAll();

        System.out.println("All the Channels: ");
        for(int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            if (channel == null) {
                return;
            }
            System.out.println(channel.getName());
        }
    }

}
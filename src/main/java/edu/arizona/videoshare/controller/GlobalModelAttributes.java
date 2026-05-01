package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.NotificationRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.service.UserMembershipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final ChannelRepository channelRepository;
    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserMembershipService userMembershipService;

    @ModelAttribute("channels")
    public List<Channel> channels(HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return Collections.emptyList();
        }

        return channelRepository.findByUserId(loggedInUserId);
    }

    @ModelAttribute("followedSubscriptions")
    public List<Subscription> followedSubscriptions(HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return Collections.emptyList();
        }

        return subscriptionRepository.findBySubscriberIdAndStatusOrderByCreatedAtDesc(
                loggedInUserId,
                Subscription.SubscriptionStatus.ACTIVE
        );
    }

    @ModelAttribute("unreadNotificationCount")
    public long unreadNotificationCount(HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return 0;
        }

        return notificationRepository.countByRecipientIdAndIsReadFalse(loggedInUserId);
    }

    @ModelAttribute("showAds")
    public boolean showAds(HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return true;
        }

        return !userMembershipService.isAdFreeUser(loggedInUserId);
    }

    @ModelAttribute("isPremiumUser")
    public boolean isPremiumUser(HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return false;
        }

        try {
            return userMembershipService.getCurrentMembership(loggedInUserId) != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
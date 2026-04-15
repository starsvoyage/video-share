package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.service.ChannelService;
import edu.arizona.videoshare.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchPageController {
    private final VideoService videoService;
    private final ChannelService channelService;

    @GetMapping("/search")
    public String showSearchResults(
            @RequestParam(name = "q", defaultValue = "") String query,
            Model model) {
        model.addAttribute("query", query);
        model.addAttribute("searchVideos", videoService.searchPublicByTitle(query));
        model.addAttribute("searchChannels", channelService.searchChannelsByName(query));
        return "search";
    }

    @GetMapping("/browse/channels")
    public String browseChannels(
            @RequestParam(name = "q", defaultValue = "") String query,
            Model model) {
        model.addAttribute("query", query);
        model.addAttribute("browseChannels",
                query.isEmpty()
                        ? channelService.getAllChannels()
                        : channelService.searchChannelsByName(query));
        return "browse-channels";
    }
}

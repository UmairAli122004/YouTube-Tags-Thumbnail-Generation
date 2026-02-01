package com.YoutubeTools.Controller;

import com.YoutubeTools.Model.SearchVideo;
import com.YoutubeTools.Service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeTagController {
    private final YoutubeService youtubeService;

    @Value("${youtube.api.key}")
    private String apiKey;

    private boolean isApiConfigured(){
        return apiKey!=null && !apiKey.isEmpty();
    }

    @PostMapping("/search")
    public String videoTags(@RequestParam("videoTitle") String videoTitle, Model model){
        if(!isApiConfigured()){
            model.addAttribute("error","Api key is not Configured");
            return "home";
        }

        if(videoTitle==null && videoTitle.isEmpty()){
            model.addAttribute("error","Video Title is Required");
            return "home";
        }

        try {
            SearchVideo result=youtubeService.searchVideos(videoTitle);
            model.addAttribute("primaryVideo",result.getPrimaryVideo());
            model.addAttribute("relatedVideos",result.getRelatedVideos());

            String allTags = result.getRelatedVideos()
                    .stream()                                        //Start stream of videos
                    .flatMap(v -> v.getTags().stream())              //Flatten all tag lists into a single stream
                    .distinct()                                      //Remove duplicates
                    .map(tag -> "#" + tag.trim())                    //Add # to each tag
                    .collect(Collectors.joining(" "));       //Convert into one string

            model.addAttribute("allTagsAsString", allTags);
            return "home";
        }catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "home";
        }
    }
}

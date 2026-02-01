package com.YoutubeTools.Controller;
import com.YoutubeTools.Model.VideoDetails;
import com.YoutubeTools.Service.ThumbnailService;
import com.YoutubeTools.Service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@RequiredArgsConstructor
@Controller
public class YoutubeVideoController {
    private final YoutubeService youtubeService;
    private final ThumbnailService thumbnailService;

//    @GetMapping("/youtube/video-details")
//    public String showVideoForm(){
//        return "video-details";
//    }

    @PostMapping("/youtube/video-details")
    public String fetchVideoDetails(@RequestParam String videoUrlOrId, Model model){
        String videoId = thumbnailService.extractVideoId(videoUrlOrId);
        if(videoId==null){
            model.addAttribute("error", "Invalid YouTube URL or ID");
            return "video-details";
        }

        VideoDetails details = youtubeService.getVideoDetails(videoId);
        if(details==null){
            model.addAttribute("error", "Video not found");
        }else{
            model.addAttribute("videoDetails", details);
        }
        model.addAttribute("videoUrlOrId", videoUrlOrId);
        return "video-details";
    }
}

package com.YoutubeTools.Controller;

import com.YoutubeTools.Service.ThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RequiredArgsConstructor
@Controller
public class ThumbnailController {

    private final ThumbnailService thumbnailService;

    @PostMapping("/get-thumbnail")
    public String showThumbnail(@RequestParam("videoUrlOrId") String videoUrlOrId, Model model){
        String videoId = thumbnailService.extractVideoId(videoUrlOrId);
        if(videoId==null){
            model.addAttribute("error", "Invalid Youtube URL");
            return "thumbnails";
        }
        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        model.addAttribute("thumbnailUrl", thumbnailUrl);
        return "thumbnails";
    }

  
    @GetMapping("/download-thumbnail")
    public ResponseEntity<byte[]> downloadThumbnail(@RequestParam String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream in = connection.getInputStream();
            byte[] imageBytes = in.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            headers.setContentDispositionFormData("attachment", "thumbnail.jpg");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


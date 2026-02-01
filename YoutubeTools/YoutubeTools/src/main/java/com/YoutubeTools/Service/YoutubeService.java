package com.YoutubeTools.Service;

import com.YoutubeTools.Model.SearchVideo;
import com.YoutubeTools.Model.Video;
import com.YoutubeTools.Model.VideoDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.YoutubeTools.DTOs.Snippet;
import com.YoutubeTools.DTOs.SearchApiResponse;
import com.YoutubeTools.DTOs.SearchItem;
import com.YoutubeTools.DTOs.VideoApiResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class YoutubeService {
    private final WebClient webClientBuilder;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.max.related.videos}")
    private Integer maxRelatedVideos;

    // FIXED CONSTRUCTOR
    public YoutubeService(WebClient.Builder webClientBuilder, @Value("${youtube.api.base.url}") String baseUrl) {
        this.webClientBuilder = webClientBuilder.baseUrl(baseUrl).build();
    }

    public SearchVideo searchVideos(String videoTitle) {
        List<String> videoIds = searchForVideoIds(videoTitle);

        if (videoIds.isEmpty()) {
            return SearchVideo.builder()
                    .primaryVideo(null)
                    .relatedVideos(Collections.emptyList())
                    .build();
        }

        String primaryVideoId = videoIds.getFirst();
        List<String> relatedVideoIds = videoIds.subList(1, Math.min(videoIds.size(), maxRelatedVideos + 1));

        Video primaryVideo = getVideoById(primaryVideoId);
        List<Video> relatedVideos = new ArrayList<>();

        for (String id : relatedVideoIds) {
            Video video = getVideoById(id);
            if (video != null) {
                relatedVideos.add(video);
            }
        }

        return SearchVideo.builder()
                .primaryVideo(primaryVideo)
                .relatedVideos(relatedVideos)
                .build();
    }

    private List<String> searchForVideoIds(String videoTitle) {

        SearchApiResponse response = webClientBuilder
                .get()
                //.uri() is used to construct the complete URL for the API call.
                //Example: https://www.googleapis.com/youtube/v3/search?part=snippet&q=java&type=video&maxResults=5&key=API_KEY
                .uri(uriBuilder -> uriBuilder
                        /*
                                uriBuilder helps you build URLs programmatically.
                                Instead of manually creating long URLs with string concatenation, you do it in a structured way.
                                uriBuilder allows you to:
                                                set the path
                                               add query parameters
                                               automatically handle encoding (example: spaces become %20)
                                               avoid mistakes in URL formatting
                                Example:
                                .uri(uriBuilder -> uriBuilder
                                    .path("/search")
                                    .queryParam("q", videoTitle)
                                    .build()
                                )
                                This produces a correct URL automatically.
                                ✅ 3. .path() — Why use it?
                                .path() is used to specify the endpoint of your API.
                                Example:
                                Base URL: https://www.googleapis.com/youtube/v3
                                Path: /search
                                So final URL becomes:
                                https://www.googleapis.com/youtube/v3/search                                                              
                                It adds the endpoint to the base URL you configured in your WebClient builder.
                        * */
                        .path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", videoTitle)
                        .queryParam("type", "video")
                        .queryParam("maxResults", maxRelatedVideos)
                        .queryParam("key", apiKey)
                        .build()
                )
                /*
                * retrieve() tells the WebClient:  “Send the request and give me the response body.”
                * It executes the HTTP request.
                *    It prepares the response so that you can extract the body.
                *    It is the simplest way to fetch the response without manually handling status codes.
                *    Without .retrieve(), the request won't execute.
                */
                .retrieve()


                /*
                * This tells WebClient:
                *    👉 “Convert the JSON response body into a Mono of SearchApiResponse object.”
                *
                * The YouTube API returns JSON.
                *    SearchApiResponse.class is your Java class that matches that JSON structure.
                * Mono<T> means:
                *     “A REACTIVE PIPELINE that will produce 0 or 1 item in the future.”
                * So the output is:
                *     Mono<SearchApiResponse>:
                *        This is NOT the ACTUAL OBJECT, but a REACTIVE wrapper that will eventually contain the object.
                *        "REACTIVE means ASYNCHRONOUS + NON-BLOCKING programming."
                *
                * */
                .bodyToMono(SearchApiResponse.class)

                .block(); //block() turns the REACTIVE RESPONSE into a NORMAL JAVA OBJECT.

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        List<String> videoIds = new ArrayList<>();
        for (SearchItem item : response.getItems()) {
            videoIds.add(item.getId().getVideoId());
        }

        return videoIds;
    }

    public VideoDetails getVideoDetails(String videoId){
        VideoApiResponse response = webClientBuilder
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", "snippet")
                        .queryParam("id", videoId)
                        .queryParam("key", apiKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();
        if(response==null || response.getItems()==null){
            return null;
        }
        Snippet snippet = response.getItems().getFirst().getSnippet();
        String thumbnail = snippet.getThumbnails().getBestThumbnailUrl();
        return VideoDetails.builder()
                .id(videoId)
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .tags(snippet.getTags()==null ? Collections.emptyList() : snippet.getTags())
                .thumbnailUrl(thumbnail)
                .channelTitle(snippet.getChannelTitle())
                .publishedAt(snippet.getPublishedAt())
                .build();
    }

    private Video getVideoById(String videoId) {

        VideoApiResponse response = webClientBuilder
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", "snippet")
                        .queryParam("id", videoId)
                        .queryParam("key", apiKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();

        if (response == null || response.getItems() == null) {
            return null;
        }

        Snippet snippet = response.getItems().getFirst().getSnippet();
        return Video.builder()
                .id(videoId)
                .title(snippet.getTitle())
                .channelTitle(snippet.getChannelTitle())
                .tags(snippet.getTags() == null ? Collections.emptyList() : snippet.getTags())
                .build();
    }
}
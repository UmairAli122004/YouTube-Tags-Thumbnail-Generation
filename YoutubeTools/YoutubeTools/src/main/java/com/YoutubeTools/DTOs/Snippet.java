package com.YoutubeTools.DTOs;

import lombok.Data;
import java.util.List;

@Data
public class Snippet {
    String title;
    String description;
    String channelTitle;
    String publishedAt;
    List<String> tags;
    Thumbnails thumbnails;
}

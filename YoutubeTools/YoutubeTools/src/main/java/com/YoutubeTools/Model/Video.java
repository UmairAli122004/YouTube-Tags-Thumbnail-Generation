package com.YoutubeTools.Model;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    private String id;
    private String channelTitle;
    private String title;
    private List<String> tags;

    public String getTagsAsString() {
        return (tags == null) ? "" : tags.stream().map(tag -> "#" + tag.trim()).collect(Collectors.joining(" "));
    }
}

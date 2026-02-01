package com.YoutubeTools.DTOs;
import lombok.Data;

import java.util.List;


@Data
public class VideoApiResponse {
    List<VideoItem> items;
}

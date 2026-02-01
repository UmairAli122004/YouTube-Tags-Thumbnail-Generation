package com.YoutubeTools.DTOs;

import lombok.Data;

@Data
public class Thumbnails {
    Thumbnail maxres;
    Thumbnail high;
    Thumbnail medium;
    Thumbnail _default;

    public String getBestThumbnailUrl(){
        if(maxres != null) return maxres.url;
        if(high != null) return high.url;
        if(medium != null) return medium.url;
        return _default != null ? _default.url : "";
    }
}

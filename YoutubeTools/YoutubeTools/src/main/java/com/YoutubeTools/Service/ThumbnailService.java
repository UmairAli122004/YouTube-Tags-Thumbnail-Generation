package com.YoutubeTools.Service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class ThumbnailService {

    public String extractVideoId(String url) {
        if (url.matches("^[a-zA-Z-0-9_-]{11}$")) {
            return url;
        }
        String[] patterns = {
                "(?:https?:\\/\\/)?(?:www\\.)?youtube\\.com\\/watch\\?v=([a-zA-Z0-9_-]{11})",
                "(?:https?:\\/\\/)?(?:www\\.)?youtu\\.be\\/([a-zA-Z0-9_-]{11})",
                "(?:https?:\\/\\/)?(?:www\\.)?youtube\\.com\\/embed\\/([a-zA-Z0-9_-]{11})"
        };
        for (String pattern : patterns) {
            /*
             *  Matcher matcher = Pattern.compile(pattern).matcher(url);
             *   Matcher:
             *          1) A type name from java.util.regex.Matcher.
             *          2) Represents the result of applying a compiled regular expression (a Pattern) to a particular input sequence.
             *          3) Provides methods like find(), matches(), group(int), start(), end(), reset() etc.
             *          4) Not thread-safe — do not share one Matcher between threads without synchronization.
             */
            Matcher matcher = Pattern.compile(pattern).matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
                /*
                 * return matcher.group(1);
                 *  Here:
                 *       v= is fixed
                 *       ([a-zA-Z0-9_-]+) is group 1
                 *       → It captures the actual video ID
                 */
            }
        }
        return null;
    }
}

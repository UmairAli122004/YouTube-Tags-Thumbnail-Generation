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

//    @GetMapping("/thumbnail")
//    public String getThumbnail(){
//        return "thumbnails";
//    }

    /*
    *
    * Endpoint	                       Purpose	                    Return Type
    * POST /get-thumbnail   	Show thumbnail preview page	      "thumbnails" (view name)
    * */
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


    /*
    *
    * ResponseEntity<byte[]>: a Spring type that represents an HTTP response,
    * including body, headers and status code. Here the body is a byte[] (raw bytes of the image).
    * Why use ResponseEntity: it gives explicit control over
    * response headers (Content-Type, Content-Disposition) and status code.
    *
    * */



    /*
     *
     * Endpoint	                             Purpose	                               Return Type
     * GET /download-thumbnail      	Actually download the image	        ResponseEntity<byte[]> (image data)
     * */
    @GetMapping("/download-thumbnail")
    public ResponseEntity<byte[]> downloadThumbnail(@RequestParam String imageUrl) {
        try {
            /*
            * URL url = new URL(imageUrl);
            * Why: URL is a Java representation of a network resource; it can open connections.
            * */
            URL url = new URL(imageUrl);

            /*
            * HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            * What: Opens a connection to the URL and casts to HttpURLConnection (works for http/https).
            * Why: To configure and perform an HTTP request (set method, headers, timeouts, etc.).
            * Class: java.net.HttpURLConnection.
            * Note: openConnection() returns a URLConnection — casting to HttpURLConnection allows HTTP-specific methods.
            * */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream in = connection.getInputStream();
            byte[] imageBytes = in.readAllBytes();

            /*
            * HttpHeaders headers = new HttpHeaders();
            * What: Creates a Spring HttpHeaders object to populate response headers.
            * Class: org.springframework.http.HttpHeaders.
            * Why: To set Content-Type and Content-Disposition for the client so the browser downloads the image.
            * */
            HttpHeaders headers = new HttpHeaders();

            /*
            *
            * headers.setContentType(MediaType.IMAGE_JPEG);
            * What: Sets the Content-Type header to image/jpeg.
            * Why: Tells the client (browser) that the response body is a JPEG image.
            * Caveat: This unconditionally sets JPEG — but the remote image might be PNG/WebP/etc. Better to detect content-type if possible.
            * Class: org.springframework.http.MediaType.
            *
            *
            *  Content-Type tells the browser:
            *    “What kind of file am I sending?”
            * */
            headers.setContentType(MediaType.IMAGE_JPEG);

            /*
            *
            *
            * headers.setContentDispositionFormData("attachment", "thumbnail.jpg");
            * What: Sets Content-Disposition: attachment; filename="thumbnail.jpg".
            * Why: Instructs browsers to download the response rather than try to display inline (and suggests a filename).
            * Result: Browser saves file as thumbnail.jpg.
            *
            *
            *
            * Content-Disposition tells the browser:
            *        “How should the browser handle this file?”
            *
            * */
            headers.setContentDispositionFormData("attachment", "thumbnail.jpg");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

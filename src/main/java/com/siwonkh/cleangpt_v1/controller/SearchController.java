package com.siwonkh.cleangpt_v1.controller;

import com.siwonkh.cleangpt_v1.model.CreatorVideo;
import com.siwonkh.cleangpt_v1.model.VideoComment;
import com.siwonkh.cleangpt_v1.model.CreatorProfile;
import com.siwonkh.cleangpt_v1.util.SearchCommentThread;
import com.siwonkh.cleangpt_v1.util.SearchCreatorProfile;
import com.siwonkh.cleangpt_v1.util.SearchCreatorVideo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    @Value("${youtubeapi.key}")
    private String APIKey;

    @GetMapping("search/creator")
    public String searchCreator(Model model, @RequestParam("creator") String creator) throws Exception {
        SearchCreatorProfile searchCreatorProfile = new SearchCreatorProfile(APIKey);
        searchCreatorProfile.setChannel(creator);
        JSONObject channels =  searchCreatorProfile.getApiResponse();

        JSONArray jsonArray = channels.getJSONArray("items");
        List<String> searchedTitles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet");
            searchedTitles.add(jsonObject.getString("title"));
        }
        List<String> searchedDesc = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet");
            searchedDesc.add(jsonObject.getString("description"));
        }
        List<String> searchedImages = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default");
            searchedImages.add(jsonObject.getString("url"));
        }
        List<String> searchedChannelIds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("id");
            searchedChannelIds.add(jsonObject.getString("channelId"));
        }

        List<CreatorProfile> creatorProfiles = new ArrayList<>();
        for (int i = 0; i < searchedTitles.size(); i++) {
            String title = searchedTitles.get(i);
            String desc = searchedDesc.get(i);
            String url = searchedImages.get(i);
            String channelId = searchedChannelIds.get(i);
            CreatorProfile creatorProfile = new CreatorProfile(title, desc, url, channelId);
            creatorProfiles.add(creatorProfile);
        }
        model.addAttribute("profiles", creatorProfiles);
        return "search/creatorProfiles";
    }

    @GetMapping("search/comment")
    public String searchComments(Model model, @RequestParam("video") String video) throws Exception {
        SearchCommentThread searchCommentThread = new SearchCommentThread(APIKey);
        searchCommentThread.setVideo(video);
        JSONObject comments =  searchCommentThread.getApiResponse();

        JSONArray jsonArray = comments.getJSONArray("items");
        List<String> searchedComments = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet");
            searchedComments.add(jsonObject.getString("textOriginal"));
        }
        List<String> searchedCommentAuthors = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet");
            searchedCommentAuthors.add(jsonObject.getString("authorDisplayName"));
        }
        List<String> searchedCommentAuthorImages = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet");
            searchedCommentAuthorImages.add(jsonObject.getString("authorProfileImageUrl"));
        }

        List<VideoComment> videoComments = new ArrayList<>();
        for (int i = 0; i < searchedComments.size(); i++) {
            String comment = searchedComments.get(i);
            String author = searchedCommentAuthors.get(i);
            String url = searchedCommentAuthorImages.get(i);
            VideoComment videoComment = new VideoComment(comment, author, url);
            videoComments.add(videoComment);
        }
        model.addAttribute("comments", videoComments);
        return "search/videoComments";
    }

    @GetMapping("search/video")
    public String searchCreatorVideos(Model model, @RequestParam("channelId") String channelId) throws Exception {
        SearchCreatorVideo searchCreatorVideo = new SearchCreatorVideo(APIKey);
        searchCreatorVideo.setChannelId(channelId);
        JSONObject videos = searchCreatorVideo.getApiResponse();

        JSONArray jsonArray = videos.getJSONArray("items");
        List<String> searchedVideoIds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("id");
            searchedVideoIds.add(jsonObject.getString("videoId"));
        }
        List<String> searchedTitles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet");
            searchedTitles.add(jsonObject.getString("title"));
        }
        List<String> searchedDescription = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet");
            searchedDescription.add(jsonObject.getString("description"));
        }
        List<String> searchedThumbnails = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium");
            searchedThumbnails.add(jsonObject.getString("url"));
        }
        List<String> searchedPublishedAt = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet");
            searchedPublishedAt.add(jsonObject.getString("publishedAt"));
        }

        List<CreatorVideo> creatorVideos = new ArrayList<>();
        for (int i = 0; i < searchedTitles.size(); i++) {
            String videoIds = searchedVideoIds.get(i);
            String title = searchedTitles.get(i);
            String description = searchedDescription.get(i);
            String thumbnail = searchedThumbnails.get(i);
            String publishedAt = searchedPublishedAt.get(i);
            CreatorVideo creatorVideo = new CreatorVideo(videoIds, title, description, thumbnail, publishedAt);
            creatorVideos.add(creatorVideo);
        }
        model.addAttribute("videos", creatorVideos);
        return "search/creatorVideos";
    }
}

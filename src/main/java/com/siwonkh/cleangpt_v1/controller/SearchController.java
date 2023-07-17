package com.siwonkh.cleangpt_v1.controller;

import com.siwonkh.cleangpt_v1.dto.SearchSessionDto;
import com.siwonkh.cleangpt_v1.entity.SearchSession;
import com.siwonkh.cleangpt_v1.model.CreatorVideo;
import com.siwonkh.cleangpt_v1.model.VideoComment;
import com.siwonkh.cleangpt_v1.model.CreatorProfile;
import com.siwonkh.cleangpt_v1.repository.SearchSessionRepository;
import com.siwonkh.cleangpt_v1.service.SearchService;
import com.siwonkh.cleangpt_v1.util.SearchCommentThread;
import com.siwonkh.cleangpt_v1.util.SearchCreatorProfile;
import com.siwonkh.cleangpt_v1.util.SearchCreatorVideo;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    @Value("${youtubeapi.key}")
    private String APIKey;

    @Value("${openai.key}")
    private String OpenAIKey;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchSessionRepository searchSessionRepository;

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
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet");
            searchedChannelIds.add(jsonObject.getString("channelId"));
        }
        List<String> searchedPublishedAt = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet");
            searchedPublishedAt.add(jsonObject.getString("publishedAt"));
        }

        List<CreatorProfile> creatorProfiles = new ArrayList<>();
        for (int i = 0; i < searchedTitles.size(); i++) {
            String title = searchedTitles.get(i);
            String desc = searchedDesc.get(i);
            String url = searchedImages.get(i);
            String channelId = searchedChannelIds.get(i);
            String publishedAt = searchedPublishedAt.get(i);
            CreatorProfile creatorProfile = new CreatorProfile(title, desc, url, channelId, publishedAt.substring(0, 10));
            creatorProfiles.add(creatorProfile);
        }
        model.addAttribute("profile", creatorProfiles.get(0));
        return "searchResult";
    }

    @GetMapping("search/comment")
    public String searchComments(Model model, @CookieValue("TOKEN") String token, @RequestParam("video") String video, @RequestParam("title") String title, @RequestParam("thumbnail") String thumbnail, @RequestParam(value = "filter", required = false, defaultValue = "") String filterParams, @RequestParam(value = "cfilter", required = false, defaultValue = "") String cFilter) throws Exception {
        SearchCommentThread searchCommentThread = new SearchCommentThread(APIKey);
        searchCommentThread.setVideo(video);
        JSONObject comments =  searchCommentThread.getApiResponse();

        JSONArray jsonArray = comments.getJSONArray("items");
        List<String> searchedComments = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet");
            searchedComments.add(jsonObject.getString("textOriginal").replaceAll(",", ""));
        }

        String commentsStr = String.join(",", searchedComments);

        SearchSession searchSession = searchService.getSearchSessionByToken(token);
        String filter = searchSession.getFilters();

//        String filter = filterParams + "," + cFilter;
        System.out.println(filter);

        List<String> filterArray = new ArrayList<>();

        if (filter != null) {
            if (filter.length() > 3) {
                filterArray = List.of(filter.split(","));

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setRole(ChatMessageRole.SYSTEM.value());
                chatMessage.setContent("You should filter list of comments based on the filter list. And output should be same format as before.");

                ChatMessage chatMessage2 = new ChatMessage();
                chatMessage2.setRole(ChatMessageRole.USER.value());
                chatMessage2.setContent(
                        "filter list: " + "Spam,Hate speech,comments more than 40 chars" + "\n"
                                + "comments: " + "I really don't like them.,https://asdhioe.com come to this link!,1020304050607080900010203040506070809000ad,This video is cool!,lets gooo,wow"
                );

                ChatMessage chatMessage3 = new ChatMessage();
                chatMessage3.setRole(ChatMessageRole.ASSISTANT.value());
                chatMessage3.setContent("This video is cool!,lets gooo,wow");

                ChatMessage chatMessage4 = new ChatMessage();
                chatMessage4.setRole(ChatMessageRole.USER.value());
                chatMessage4.setContent(
                        "filter list: " + filter + "\n"
                                + "comments: " + commentsStr
                );

                List<ChatMessage> chatMessages = new ArrayList<>();
                chatMessages.add(chatMessage);
                chatMessages.add(chatMessage2);
                chatMessages.add(chatMessage3);
                chatMessages.add(chatMessage4);

                OpenAiService service = new OpenAiService(OpenAIKey);

                ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                        .model("gpt-3.5-turbo-16k")
                        .messages(chatMessages)
                        .build();
                String reply = service.createChatCompletion(completionRequest).getChoices().get(0).getMessage().getContent();
                System.out.println(reply);

//            Type type = new TypeToken<List<VideoComment>>() {}.getType();
//            videoComments = new Gson().fromJson(videoCommentsJson, type);
                searchedComments = List.of(reply.split(","));
                model.addAttribute("customs", filter.replace("spam,", "").replace("abuse,", "").replace("hatespeech,", "").split(","));

            }
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
        for (int i = 0; i < searchedComments.size()-1; i++) {
            String comment = searchedComments.get(i);
            String author = searchedCommentAuthors.get(i);
            String url = searchedCommentAuthorImages.get(i);
            VideoComment videoComment = new VideoComment(comment, author, url);
            videoComments.add(videoComment);
        }

        model.addAttribute("comments", videoComments);
        model.addAttribute("videoTitle", title);
        model.addAttribute("videoThumbnail", thumbnail);
        model.addAttribute("commentCount", videoComments.size());
        model.addAttribute("hasSpam", filterArray.contains("spam"));
        model.addAttribute("hasAbuse", filterArray.contains("abuse"));
        model.addAttribute("hasHatespeech", filterArray.contains("hatespeech"));
        return "comment";
    }

    @PostMapping("search/start")
    public String createSearchSession(HttpServletResponse response, @RequestParam("creatorId") String creatorId) throws Exception {
        SearchSessionDto searchSessionDto = new SearchSessionDto();
        searchSessionDto.setCreatorId(creatorId);

        searchSessionDto = searchService.createSearchSession(searchSessionDto);
        Cookie cookie = new Cookie("TOKEN", searchSessionDto.getSearchSessionToken());
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/videos";
    }

    @PostMapping("search/comment/filter/add")
    public void addFilter(HttpServletResponse response, @CookieValue("TOKEN") String token, @RequestParam("filter") String filter) throws Exception {
        SearchSession searchSession = searchService.getSearchSessionByToken(token);
        String filters = searchSession.getFilters();
        List<String> filterArray = new ArrayList<>();
        if (filters != null) {
            filterArray = new ArrayList<>(List.of(filters.split(",")));
        }

        if (!filterArray.contains(filter)) {
            filterArray.add(filter);
        }
        searchSession.setFilters(String.join(",", filterArray));
        searchSessionRepository.save(searchSession);
    }
    @PostMapping("search/comment/filter/remove")
    public void removeFilter(HttpServletResponse response, @CookieValue("TOKEN") String token, @RequestParam("filter") String filter) throws Exception {
        SearchSession searchSession = searchService.getSearchSessionByToken(token);
        String filters = searchSession.getFilters();
        List<String> filterArray = new ArrayList<>();
        if (filters != null) {
            filterArray = new ArrayList<>(List.of(filters.split(",")));
        }
        filterArray.remove(filter);

        searchSession.setFilters(String.join(",", filterArray));
        searchSessionRepository.save(searchSession);
    }

    @GetMapping("videos")
    public String searchCreatorVideos(Model model, @CookieValue("TOKEN") String token) throws Exception {
        SearchSession searchSession = searchService.getSearchSessionByToken(token);
        String creatorId = searchSession.getCreatorId();

        SearchCreatorVideo searchCreatorVideo = new SearchCreatorVideo(APIKey);
        searchCreatorVideo.setChannelId(creatorId);
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
            CreatorVideo creatorVideo = new CreatorVideo(videoIds, title, description, thumbnail, publishedAt.substring(0, 10));
            creatorVideos.add(creatorVideo);
        }
        model.addAttribute("videos", creatorVideos);
        return "video";
    }
}

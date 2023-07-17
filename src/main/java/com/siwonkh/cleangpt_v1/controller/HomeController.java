package com.siwonkh.cleangpt_v1.controller;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Value("${openai.key}")
    private String APIKey;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/comment")
    public String comments() {
        return "searchVideoComments";
    }

    @GetMapping("/test")
    public String testOpenAI(Model model) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole(ChatMessageRole.USER.value());
        chatMessage.setContent("Say test!");

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(chatMessage);

        OpenAiService service = new OpenAiService(APIKey);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .maxTokens(512)
                .temperature(0.7)
                .topP(1.0)
                .messages(chatMessages)
                .build();
        String reply = service.createChatCompletion(completionRequest).getChoices().get(0).getMessage().getContent();
        System.out.println(reply);
        System.out.println("safa");
        model.addAttribute("reply", reply);
        return "test";
    }
}

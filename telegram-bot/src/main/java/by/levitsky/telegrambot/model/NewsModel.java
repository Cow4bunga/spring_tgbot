package by.levitsky.telegrambot.model;

import lombok.Data;

@Data
public class NewsModel {
    String source;
    String title;
    String description;
    String url;
    String content;
}

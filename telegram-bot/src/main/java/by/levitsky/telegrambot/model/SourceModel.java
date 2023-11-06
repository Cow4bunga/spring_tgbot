package by.levitsky.telegrambot.model;

import lombok.Data;

@Data
public class SourceModel {
    String source;
    String description;
    String url;
    String category;
    String language;
    String country;
}

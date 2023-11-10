package by.levitsky.telegrambot.service;

import by.levitsky.telegrambot.config.BotConfig;
import by.levitsky.telegrambot.dto.NoteDto;
import by.levitsky.telegrambot.util.Constants;
import by.levitsky.telegrambot.model.*;
import by.levitsky.telegrambot.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteService noteService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private NewsService newsService;
    static final List<BotCommand> botCommandList = new ArrayList<>();
    static Set<String> currencySet = new HashSet<>();

    private int flag = 0;
    static final String HELP_MESSAGE = "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
//            "Type /stats to see statistics for your data\n\n" +
            "Type /data to see the contents of your notes\n\n" +
            "Type /delete to remove note/-s from your data\n\n" +
            "Type /help to see this message again";

    static {
        botCommandList.add(new BotCommand("/start", "get a welcome message"));
//        botCommandList.add(new BotCommand("/stats", "see statistics"));
        botCommandList.add(new BotCommand("/data", "see saved data"));
        botCommandList.add(new BotCommand("/delete", "delete notes chosen by user (if present)"));
        botCommandList.add(new BotCommand("/help", "get the description of commands"));
        try {
            currencySet = CurrencyService.getCurrenciesList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TelegramBot(@Value("${bot.token}") String token, BotConfig config) {
        super(token);
        this.config = config;
        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error creating bot instance: " + e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            long chatId = update.getMessage().getChatId();

            if (message.contains("/send") && config.getOwnerId() == chatId) {
                String textToSend = EmojiParser.parseToUnicode(message.substring(message.indexOf(" ")));
                Iterable<User> users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getId(), textToSend);
                }
            } else if (currencySet.contains(message) && flag == 1) {
                CurrencyModel model = new CurrencyModel();
                try {
                    String currencyType = currencyService.getCurrencyRates(message, model);
                    sendMessage(chatId, currencyType);
                    flag = 0;
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (Character.isUpperCase(message.charAt(0)) && flag == 21) {
                WeatherReport report = new WeatherReport();
                try {
                    String weather = weatherService.getRealTimeForecast(message, report);
                    sendMessage(chatId, weather);
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (Character.isUpperCase(message.charAt(0)) && flag == 22) {
                ForecastModel report = new ForecastModel();
                try {
                    String weather = weatherService.getForecast(message, report);
                    sendMessage(chatId, weather);
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (flag == 31) {
                NewsModel newsModel = new NewsModel();
                List<String> articles;
                try {
                    articles = newsService.getEverythingByKeyword(message, newsModel);
                    for (String article : articles) {
                        sendMessage(chatId, article);
                    }
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (flag == 32) {
                NewsModel newsModel = new NewsModel();
                List<String> articles;
                try {
                    articles = newsService.getHeadlinesByCountry(message, newsModel);
                    for (String article : articles) {
                        sendMessage(chatId, article);
                    }
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (flag == 33) {
                NewsModel newsModel = new NewsModel();
                List<String> articles;
                try {
                    articles = newsService.getHeadlinesByCategory(message, newsModel);
                    for (String article : articles) {
                        sendMessage(chatId, article);
                    }
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (flag == 34) {
                NewsModel newsModel = new NewsModel();
                List<String> articles;
                try {
                    articles = newsService.getHeadlinesBySource(message, newsModel);
                    for (String article : articles) {
                        sendMessage(chatId, article);
                    }
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (flag == 35) {
                SourceModel sourceModel = new SourceModel();
                List<String> sources;
                try {
                    sources = newsService.getSourcesByCountry(message, sourceModel);
                    for (String source : sources) {
                        sendMessage(chatId, source);
                    }
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (flag == 36) {
                SourceModel sourceModel = new SourceModel();
                List<String> sources;
                try {
                    sources = newsService.getSourcesByCategory(message, sourceModel);
                    for (String source : sources) {
                        sendMessage(chatId, source);
                    }
                    flag = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (flag == 41) {
                NoteDto noteDto = new NoteDto();
                noteDto.setTitle(message.substring(0, message.indexOf("\n")));
                noteDto.setNote(message.substring(message.indexOf("\n") + 1));
                noteService.createNote(noteDto, chatId);
                prepareAndSendMessage(chatId, "Successfully created note!");
                flag = 0;
            } else if (flag == 42) {
                flag = 0;
            } else if (flag == 43) {
                noteService.deleteNote(chatId, Long.valueOf(message));
                prepareAndSendMessage(chatId, "Successfully deleted note!");
                flag = 0;
            } else {
                switch (message) {
                    case "/start" -> {
                        registerUser(update.getMessage());
                        startCmdReceived(chatId
                                , update.getMessage().getChat().getFirstName());
                        break;
                    }
                    case "/help" -> {
                        prepareAndSendMessage(chatId, HELP_MESSAGE);
                        break;
                    }
                    case "/data" -> {
                        prepareAndSendMessage(chatId, getNotes(noteService.getAllNotesByUser(chatId)));
                    }
                    case "currency rates" -> {
                        getCurrency(chatId);
                        flag = 1;
                    }
                    case "current weather" -> {
                        getWeatherReport(chatId);
                        flag = 2;
                    }
                    case "news" -> {
                        getNews(chatId);
                        flag = 3;
                    }
                    case "add note" -> {
                        flag=0;
                        prepareAndSendMessage(chatId, "Please, enter note in the following format:\n" +
                                "First line-title of the note\n" +
                                "Next lines-note's content");
                        flag = 41;
                    }
                    case "edit note" -> {
                        flag = 42;
                    }
                    case "delete note" -> {
                        prepareAndSendMessage(chatId, "Please, enter the ID of the note to be deleted:");
                        flag = 43;
                    }
                    default -> {
                        prepareAndSendMessage(chatId, "Sorry, command is not supported!");
                        break;
                    }
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String response;

            if (currencySet.contains(callbackData)) {
                try {
                    flag = 0;
                    response = currencyService.getCurrencyRates(callbackData, new CurrencyModel());
                    executeEditMessageText(response, chatId, messageId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (callbackData.equals(Constants.LIVE) || callbackData.equals(Constants.FUTURE)) {
                response = "Please, choose the city-simply type it as following: Minsk";
                switch (callbackData) {
                    case Constants.LIVE -> {
                        flag = 21;
                        executeEditMessageText(response, chatId, messageId);
                    }
                    case Constants.FUTURE -> {
                        flag = 22;
                        executeEditMessageText(response, chatId, messageId);
                    }
                    default -> {
                        prepareAndSendMessage(chatId, "Sorry, command is not supported!");
                        break;
                    }
                }
            } else if (callbackData.equals(Constants.ALL)) {
                response = "Please, enter a keyword to search corresponding articles";
                flag = 31;
                executeEditMessageText(response, chatId, messageId);
            } else if (callbackData.equals(Constants.HEAD_CTR)) {
                response = "Please, enter a country to search corresponding headlines";
                flag = 32;
                executeEditMessageText(response, chatId, messageId);
            } else if (callbackData.equals(Constants.HEAD_CAT)) {
                response = "Please, enter a category to search corresponding headlines";
                flag = 33;
                executeEditMessageText(response, chatId, messageId);
            } else if (callbackData.equals(Constants.HEAD_SRC)) {
                response = "Please, enter a source to search corresponding headlines";
                flag = 34;
                executeEditMessageText(response, chatId, messageId);
            } else if (callbackData.equals(Constants.SOURCE_CTR)) {
                response = "Please, enter a country to search corresponding sources";
                flag = 35;
                executeEditMessageText(response, chatId, messageId);
            } else if (callbackData.equals(Constants.SOURCE_CAT)) {
                response = "Please, enter a category to search corresponding sources";
                flag = 36;
                executeEditMessageText(response, chatId, messageId);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    private void startCmdReceived(long chatId, String name) {
        String response = EmojiParser.parseToUnicode("Hello, " + name + ", nice to meet you!" + ":smiley:");
        log.info("Replied to user " + name);
        sendMessage(chatId, response);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        message.setReplyMarkup(createKeyboardMarkup());

        executeMessage(message);
    }

    private void registerUser(Message message) {
        if (!userRepository.existsById(message.getChatId())) {
            long chatId = message.getChatId();
            Chat chat = message.getChat();

            User newUser = new User();
            newUser.setId(message.getChatId());
            newUser.setFirstname(chat.getFirstName());
            newUser.setLastname(chat.getLastName());
            newUser.setUsername(chat.getUserName());
            newUser.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(newUser);
        }
    }

    private ReplyKeyboardMarkup createKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("current weather");
        row.add("currency rates");
        row.add("news");
        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("add note");
        row.add("edit note");
        row.add("delete note");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    private String getNotes(Iterable<NoteDto> notes) {
        StringBuilder sb = new StringBuilder();
        for (NoteDto note : notes) {
            sb.append("ID: " + note.getId()
                    + "\n" + note.getTitle()
                    + "\n" + note.getNote() + "\n\n");
        }
        return String.valueOf(sb);
    }

    private void addNote() {

    }

    // TODO: Create better query processing
    private void getCurrency(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please, choose the currency: if there is no button representing your " +
                "currency, simply type it, example for canadian dollar: CAD.");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton usdButton = new InlineKeyboardButton();
        InlineKeyboardButton rubButton = new InlineKeyboardButton();
        InlineKeyboardButton eurButton = new InlineKeyboardButton();
        InlineKeyboardButton uahButton = new InlineKeyboardButton();
        InlineKeyboardButton plnButton = new InlineKeyboardButton();
        InlineKeyboardButton gbpButton = new InlineKeyboardButton();

        usdButton.setText("USD");
        rubButton.setText("RUB");
        eurButton.setText("EUR");
        uahButton.setText("UAH");
        plnButton.setText("PLN");
        gbpButton.setText("GBP");

        usdButton.setCallbackData(Constants.USD);
        rubButton.setCallbackData(Constants.RUB);
        eurButton.setCallbackData(Constants.EUR);
        uahButton.setCallbackData(Constants.UAH);
        plnButton.setCallbackData(Constants.PLN);
        gbpButton.setCallbackData(Constants.GBP);


        rowInLine.add(usdButton);
        rowInLine.add(rubButton);
        rowInLine.add(eurButton);
        rowsInLine.add(rowInLine);

        rowInLine = new ArrayList<>();

        rowInLine.add(uahButton);
        rowInLine.add(plnButton);
        rowInLine.add(gbpButton);
        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void getWeatherReport(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please, choose the option:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton liveButton = new InlineKeyboardButton();
        InlineKeyboardButton futureButton = new InlineKeyboardButton();

        liveButton.setText("live forecast");
        futureButton.setText("future weather");
        liveButton.setCallbackData(Constants.LIVE);
        futureButton.setCallbackData(Constants.FUTURE);

        rowInLine.add(liveButton);
        rowInLine.add(futureButton);
        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    // TODO: Implement newsletter dispatching
    private void getNews(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please, choose the option:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton everythingButton = new InlineKeyboardButton();
        InlineKeyboardButton headlinesByCountryButton = new InlineKeyboardButton();
        InlineKeyboardButton headlinesByCategoryButton = new InlineKeyboardButton();
        InlineKeyboardButton headlinesBySourceButton = new InlineKeyboardButton();
        InlineKeyboardButton sourcesByCountryButton = new InlineKeyboardButton();
        InlineKeyboardButton sourcesByCategoryButton = new InlineKeyboardButton();


        everythingButton.setText("Get news by keyword");
        headlinesByCountryButton.setText("Get top headlines by country");
        headlinesByCategoryButton.setText("Get top headlines by category");
        headlinesBySourceButton.setText("Get top headlines by source");
        sourcesByCategoryButton.setText("Get top sources by category");
        sourcesByCountryButton.setText("Get top sources by country");

        everythingButton.setCallbackData(Constants.ALL);
        headlinesByCountryButton.setCallbackData(Constants.HEAD_CTR);
        headlinesByCategoryButton.setCallbackData(Constants.HEAD_CAT);
        headlinesBySourceButton.setCallbackData(Constants.HEAD_SRC);
        sourcesByCategoryButton.setCallbackData(Constants.SOURCE_CAT);
        sourcesByCountryButton.setCallbackData(Constants.SOURCE_CTR);

        rowInLine.add(everythingButton);
        rowInLine.add(headlinesByCountryButton);
        rowInLine.add(headlinesByCategoryButton);
        rowsInLine.add(rowInLine);

        rowInLine = new ArrayList<>();

        rowInLine.add(headlinesBySourceButton);
        rowInLine.add(sourcesByCountryButton);
        rowInLine.add(sourcesByCategoryButton);

        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText messageText = new EditMessageText();
        messageText.setChatId(String.valueOf(chatId));
        messageText.setText(text);
        messageText.setMessageId((int) messageId);

        try {
            execute(messageText);
        } catch (TelegramApiException e) {
            log.error("Error while editing message: " + e);
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while executing message: " + e);
        }
    }

    @Scheduled(cron = "${cron.schedule}")
    private void sendNotes() {
        Iterable<NoteDto> notes = noteService.getAllNotes();
        Iterable<User> users = userRepository.findAll();

        for (NoteDto note : notes) {
            for (User user : users) {
                prepareAndSendMessage(user.getId(), note.getNote());
            }
        }
    }
}

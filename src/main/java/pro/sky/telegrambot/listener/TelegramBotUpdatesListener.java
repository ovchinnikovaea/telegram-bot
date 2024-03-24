package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.configuration.model.NotificationTask;
import pro.sky.telegrambot.configuration.repository.NotificationTaskRepository;
import pro.sky.telegrambot.configuration.service.TelegramBotSender;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationTaskRepository notificationTaskRepository;

    private final TelegramBot telegramBot;

    private final TelegramBotSender telegramBotSender;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final String WELCOME_MASSEGE = "Добро пожаловать в напоминалку!";
    private final String SUCCESSFUL_SAVE_MESSAGE = "Напоминание успешно сохранено!";
    private final Pattern INCOMING_MESSAGE_PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot, TelegramBotSender telegramBotSender) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
        this.telegramBotSender = telegramBotSender;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Long chatId = update.message().chat().id();
            String inputText = update.message().text();

            if(inputText.equals("/start")) {
                telegramBotSender.send(chatId, WELCOME_MASSEGE);
            } else {
                Matcher matcher = INCOMING_MESSAGE_PATTERN.matcher(inputText);
                if (matcher.matches()) {
                    String date = matcher.group(1);
                    LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    String item = matcher.group(3);
                    notificationTaskRepository.save(new NotificationTask(chatId, item, localDateTime));
                    telegramBotSender.send(chatId, SUCCESSFUL_SAVE_MESSAGE);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}

package pro.sky.telegrambot.configuration.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.configuration.model.NotificationTask;
import pro.sky.telegrambot.configuration.repository.NotificationTaskRepository;
import pro.sky.telegrambot.configuration.service.TelegramBotSender;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationSenderScheduled {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskRepository notificationTaskRepository;

    private final TelegramBotSender telegramBotSender;

    public NotificationSenderScheduled(NotificationTaskRepository notificationTaskRepository, TelegramBotSender telegramBotSender) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBotSender = telegramBotSender;
    }


    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void sendNotification() {

        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<NotificationTask> allByDate = notificationTaskRepository.findALLByDate(localDateTime);

        for (NotificationTask notificationTask : allByDate) {

            telegramBotSender.send(notificationTask.getChatId(), "Вы просили напомнить " + notificationTask.getMassage());
        }

    }

}

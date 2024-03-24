package pro.sky.telegrambot.configuration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.configuration.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findALLByDate(LocalDateTime date);
}

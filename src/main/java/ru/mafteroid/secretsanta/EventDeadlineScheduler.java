package ru.mafteroid.secretsanta;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mafteroid.secretsanta.service.EventService;

@Component
public class EventDeadlineScheduler {

    private final EventService eventService;

    public EventDeadlineScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(fixedRate = 10_000)
    public void startEvents(){
        eventService.startExpiredEvents();
    }
}

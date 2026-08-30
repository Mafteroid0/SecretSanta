package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Component;
import ru.mafteroid.secretsanta.entity.EventParticipant;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GiftAssignmentGenerator {
    public void assign(List<EventParticipant> participants) {
        if (participants.size() < 2) {
            throw new BadRequestException(
                    "At least 2 participants are required"
            );
        }

        List<EventParticipant> shuffled =
                new ArrayList<>(participants);

        Collections.shuffle(shuffled);

        for (int i = 0; i < shuffled.size(); i++) {
            EventParticipant giver = shuffled.get(i);

            EventParticipant receiver =
                    shuffled.get((i + 1) % shuffled.size());

            User giftedUser = receiver.getUser();

            giver.assignGiftedUser(giftedUser);
        }
    }
}

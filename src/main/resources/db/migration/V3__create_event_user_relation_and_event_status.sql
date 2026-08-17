ALTER TABLE events
    ADD COLUMN started BOOLEAN NOT NULL DEFAULT FALSE;


CREATE TABLE event_participants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    event_id UUID NOT NULL,
    user_id UUID NOT NULL,
    gifted_user_id UUID,

    CONSTRAINT fk_event_participant_event
        FOREIGN KEY (event_id)
            REFERENCES events(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_event_participant_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_event_participant_gifted_user
        FOREIGN KEY (gifted_user_id)
            REFERENCES users(id)
            ON DELETE SET NULL,

    CONSTRAINT uq_event_participant
        UNIQUE (event_id, user_id)
);
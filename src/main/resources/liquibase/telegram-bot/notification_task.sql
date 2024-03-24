-- liquibase formatted sql

-- changeset ovchinnikova:1
CREATE SEQUENCE global_seq;

CREATE TABLE notification_task (
    id INTEGER PRIMARY KEY DEFAULT nextval ('global_seq'),
    chat_id INTEGER not null,
    message TEXT not null,
    date TIMESTAMP not null
)

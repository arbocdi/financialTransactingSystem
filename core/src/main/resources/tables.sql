--inbox
create table inbox_events
(
    message_id uuid not null primary key
);
create table inbox_commands
(
    message_id uuid not null primary key
);
--outbox
create table outbox_events
(
    event_id  uuid      not null primary key,
    payload   jsonb      not null,
    state     text      not null,
    seq_number bigserial not null
);
create index outbox_events_idx on outbox_events (state,seq_number);
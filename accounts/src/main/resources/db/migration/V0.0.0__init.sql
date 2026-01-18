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
    event_id   uuid      not null primary key,
    key     text not null,
    topic   text not null,
    payload    jsonb     not null,
    headers text not null,
    seq_number bigserial not null
);
create index outbox_seq_num_idx on outbox_events (seq_number);
--accounts
create table accounts
(
    id       uuid    not null primary key,
    owner_id uuid    not null,
    balance  integer not null
);
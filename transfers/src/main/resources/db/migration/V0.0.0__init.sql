--inbox
create table inbox_events
(
    message_id uuid not null primary key
);
--outbox
create table outbox_events
(
    event_id   uuid      not null primary key,
    payload    jsonb     not null,
    state      text      not null,
    seq_number bigserial not null
);
create index outbox_events_idx on outbox_events (state, seq_number);
--sagas
create table transfer_sagas
(
    id                uuid    not null primary key,
    source_account_id uuid    not null,
    target_account_id uuid    not null,
    amount            integer not null,
    state             text    not null,
    last_error        text,
    withdraw_cmd      jsonb   not null,
    compensate_cmd    jsonb   not null,
    deposit_cmd       jsonb   not null,
    in_terminal_state boolean not null
);
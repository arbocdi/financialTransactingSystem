--accounts
create table accounts
(
    id       uuid    not null primary key,
    owner_id uuid    not null,
    balance  integer not null
);
create index accounts_owner_id_idx on accounts (owner_id);
--processed commands
create table inbox_commands
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
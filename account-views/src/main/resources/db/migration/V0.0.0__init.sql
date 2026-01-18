--inbox
create table inbox_events
(
    message_id uuid not null primary key
);
--accounts
create table accounts
(
    id       uuid    not null primary key,
    owner_id uuid    not null,
    balance  integer not null
);
create index accounts_owner_id_idx on accounts (owner_id);
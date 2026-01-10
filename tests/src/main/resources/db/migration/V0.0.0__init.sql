--inbox
create table inbox_events
(
    id uuid not null primary key
);
--sagas
create table transfer_sagas
(
    id               uuid    not null primary key,
    source_account_id uuid    not null,
    target_account_id uuid    not null,
    amount           integer not null,
    state            text    not null,
    last_error       text,
    withdraw_cmd     jsonb   not null,
    compensate_cmd   jsonb   not null,
    deposit_cmd       jsonb   not null,
    in_terminal_state boolean not null
);
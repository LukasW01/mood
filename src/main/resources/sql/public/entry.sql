create table if not exists public.entry
(
    id      uuid not null
        constraint entry_pkey
            primary key,
    color   varchar(255) not null,
    date    timestamp(6) not null,
    journal varchar(1000),
    mood    varchar(255),
    user_id bigint
        constraint entry_user_id_fkey
            references public.users
);


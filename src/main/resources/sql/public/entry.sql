create table if not exists public.entry
(
    id      uuid not null
        constraint entry_pkey
            primary key,
    color   varchar(255),
    date    timestamp(6),
    journal varchar(255),
    mood    varchar(255),
    user_id bigint
        constraint entry_user_id_fkey
            references public.users
);


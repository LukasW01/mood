create table if not exists public.users
(
    id         bigserial
        constraint users_pkey
            primary key,
    datejoined timestamp(6),
    isverified boolean not null,
    mail       varchar(255)
        constraint mail_index
            unique,
    password   varchar(255),
    token      uuid,
    username   varchar(255)
        constraint username_index
            unique,
    firstname  varchar(255),
    lastname   varchar(255)
);


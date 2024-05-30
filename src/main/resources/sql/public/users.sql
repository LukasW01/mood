create table if not exists public.users
(
    id          bigserial
        constraint users_pkey
            primary key,
    datejoined  timestamp(6),
    firstname   varchar(255),
    isverified  boolean not null,
    lastname    varchar(255),
    mail        varchar(255)
        constraint mail_index
            unique,
    password    varchar(255),
    resettoken  uuid,
    verifytoken uuid
);


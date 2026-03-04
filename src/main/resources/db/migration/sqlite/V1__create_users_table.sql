create table if not exists users
(
    id           uuid         not null
        constraint users_pkey
            primary key,
    datejoined   timestamp(6),
    firstname    varchar(255),
    isverified   boolean      not null,
    lastname     varchar(255),
    mail         varchar(255) not null
        constraint mail_index
            unique,
    password     varchar(255) not null,
    resettoken   uuid,
    sharingtoken uuid,
    verifytoken  uuid         not null
);


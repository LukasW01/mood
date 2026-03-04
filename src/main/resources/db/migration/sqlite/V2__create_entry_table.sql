create table if not exists entry
(
    id      integer 
        constraint entry_pkey 
            primary key 
        autoincrement,
    color   varchar(255) not null,
    date    date         not null,
    journal varchar(255),
    mood    varchar(255) not null,
    user_id uuid
        constraint constraint_on_delete_entry
            references users
            on delete cascade
);

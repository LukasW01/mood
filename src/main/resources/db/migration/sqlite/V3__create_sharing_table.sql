create table if not exists sharing
(
    id           integer
        constraint sharing_pkey
            primary key
        autoincrement,
    createdat    timestamp(6),
    permissions  smallint
        constraint sharing_permissions_check
            check ((permissions >= 0) AND (permissions <= 3)),
    updatedat    timestamp(6),
    delegator_id uuid
        constraint constraint_on_delete_shareing_delegator
            references users
            on delete cascade,
    user_id      uuid
        constraint constraint_on_delete_shareing_user
            references users
            on delete cascade
);

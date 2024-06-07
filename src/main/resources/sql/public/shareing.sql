create table public.shareing (
    id bigint primary key not null default nextval('shareing_id_seq'::regclass),
    createdat timestamp(6) with time zone,
    updatedat timestamp(6) with time zone,
    delegator_id bigint,
    user_id bigint,
    foreign key (delegator_id) references public.users (id)
    match simple on update no action on delete cascade,
    foreign key (user_id) references public.users (id)
    match simple on update no action on delete cascade
);


create table public.sharing (
    id bigint primary key not null default nextval('sharing_id_seq'::regclass),
    createdat timestamp(6) with time zone,
    permission smallint,
    updatedat timestamp(6) with time zone,
    delegator_id bigint,
    user_id bigint,
    foreign key (user_id) references public.users (id) 
    match simple on update no action on delete cascade,
    foreign key (delegator_id) references public.users (id) 
    match simple on update no action on delete cascade
);

create unique index sharing_pkey on public.sharing (id);

alter table public.sharing add constraint sharing_permission_check check ((permission >= 0) AND (permission <= 3));

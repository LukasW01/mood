create table public.entry (
    id uuid primary key not null,
    color character varying(255),
    date timestamp(6) without time zone,
    journal character varying(255),
    mood character varying(255),
    user_id bigint,
    foreign key (user_id) references public.users (id) on delete cascade
);

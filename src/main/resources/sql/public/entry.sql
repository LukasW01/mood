create table public.entry (
  id uuid primary key not null,
  color character varying(255),
  date date,
  journal character varying(255),
  mood character varying(255),
  user_id bigint,
  foreign key (user_id) references public.users (id)
  match simple on update no action on delete cascade
);


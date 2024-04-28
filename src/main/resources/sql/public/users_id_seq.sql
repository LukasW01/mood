create sequence public.users_id_seq;

alter sequence public.users_id_seq owned by public.users.id;


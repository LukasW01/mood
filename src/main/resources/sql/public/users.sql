create table public.users (
  id bigint primary key not null default nextval('users_id_seq'::regclass),
  datejoined timestamp(6) without time zone,
  firstname character varying(255),
  isverified boolean not null,
  lastname character varying(255),
  mail character varying(255),
  password character varying(255),
  resettoken uuid,
  sharingtoken uuid,
  verifytoken uuid
);

create unique index users_pkey on public.users (id);

create unique index mail_index on users using btree (mail);

alter table public.users add constraint mail_index unique (mail);



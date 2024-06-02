create table public.users (
  id bigint primary key not null default nextval('users_id_seq'::regclass),
  datejoined timestamp(6) without time zone,
  firstname character varying(255),
  isverified boolean not null,
  lastname character varying(255),
  mail character varying(255),
  password character varying(255),
  resettoken uuid,
  verifytoken uuid
);

create unique index mail_index on users using btree (mail);


-- auto-generated definition
create table users
(
  first_name     varchar(120)               not null,
  last_name      varchar(120),
  email_address  varchar(120)               not null,
  user_password  varchar(120)               not null,
  gender         char default 'N' :: bpchar not null,
  email_verified boolean default false      not null,
  admin_approved boolean default true       not null,
  created_date   bigint                     not null,
  updated_date   bigint,
  user_seq_id    serial                     not null
    constraint users_pk
    primary key
);
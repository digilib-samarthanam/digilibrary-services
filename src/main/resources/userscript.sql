CREATE TABLE public.users (
  first_name varchar(120) NOT NULL,
  last_name varchar(120) NULL,
  mobile_number varchar(10) NULL,
  email_address varchar(120) NOT NULL,
  user_password varchar(120) NOT NULL,
  gender CHAR(1) NOT NULL DEFAULT 'N',
  email_verified bool NOT NULL DEFAULT false,
  admin_approved bool NOT NULL DEFAULT true,
  created_date BIGINT NOT NULL,
  updated_date BIGINT NULL,
  user_seq_id serial NOT NULL,
  CONSTRAINT users_pk PRIMARY KEY (user_seq_id)
);
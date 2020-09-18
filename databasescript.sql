
-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
	user_id varchar(64) NOT NULL,
	first_name varchar(120) NOT NULL,
	last_name varchar(120) NULL,
	mobile_number int4 NULL,
	address varchar(999) NULL,
	email_address varchar(120) NOT NULL,
	email_verified bool NOT NULL DEFAULT false,
	admin_approved bool NOT NULL DEFAULT true,
	created_date timestamptz NOT NULL,
	updated_date timestamptz NULL,
	user_password varchar(120) NOT NULL,
	user_seq_id serial NOT NULL,
	city varchar(199) NULL,
	state varchar(199) NULL,
	country varchar(199) NULL,
	pin_code int4 NULL,
	CONSTRAINT users_pk PRIMARY KEY (user_seq_id)
);

-- Drop table

-- DROP TABLE public.user_email_validation;

CREATE TABLE public.user_email_validation (
	user_seq_id int4 NOT NULL,
	generated_otp_value int4 NOT NULL,
	user_email_seq_id serial NOT NULL,
	created_ts timestamptz NOT NULL,
	is_expired bool NOT NULL DEFAULT false,
	is_verified bool NOT NULL DEFAULT false,
	CONSTRAINT user_email_validation_pk PRIMARY KEY (user_email_seq_id),
	CONSTRAINT user_email_validation_fk FOREIGN KEY (user_seq_id) REFERENCES users(user_seq_id)
);

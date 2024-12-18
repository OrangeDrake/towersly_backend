
--nezkontrolovano
CREATE TABLE IF NOT EXISTS public.user
(
    id serial PRIMARY KEY,
    name character varying(255) UNIQUE NOT NULL CHECK (name <> ''),
    next_distribution_rank integer NOT NULL,
    next_shelf_rank integer NOT NULL,
    visible_works integer NOT NULL,
    tracking jsonb
);

--ALTER TABLE public.user ADD COLUMN visible_works integer;
--UPDATE public.user SET visible_works = 5;


CREATE TABLE IF NOT EXISTS public.shelf
(
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL CHECK (name <> ''),
    is_active boolean NOT NULL,
    rank integer NOT NULL,
    next_work_rank integer NOT NULL,
    user_id int NOT NULL REFERENCES public.user(id),
    UNIQUE (user_id, name)
);

ALTER TABLE public.user
ADD COLUMN tracking jsonb;

CREATE TABLE IF NOT EXISTS public.work
(
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL CHECK (name <> ''),
    is_completed boolean NOT NULL,
    rank integer NOT NULL,
    description character varying(255),
    expected_time integer NOT NULL,
    actual_time integer NOT NULL,
    shelf_id bigint NOT NULL REFERENCES public.shelf(id),
    UNIQUE (shelf_id, name)
);

CREATE TABLE IF NOT EXISTS public.distribution
(
    id bigserial PRIMARY KEY,
    name character varying(255) UNIQUE NOT NULL CHECK (name <> ''),
    is_active boolean NOT NULL,
    rank integer NOT NULL,
    projection jsonb,
    connection jsonb,
    user_id bigint NOT NULL REFERENCES public.user(id),
    UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS public.calendar (
    id bigserial PRIMARY KEY,
    year_and_weeknumber integer NOT NULL,
    plan jsonb,
    user_id NOT NULL REFERENCES public.user(id)
);

CREATE INDEX year_weeknumber_user
ON public.calendar (year_and_weeknumber, user_id);



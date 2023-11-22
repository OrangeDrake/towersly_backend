CREATE TABLE IF NOT EXISTS public.user
(
    id serial PRIMARY KEY,
    name character varying(255) UNIQUE NOT NULL CHECK (name <> ''),
    next_distribution_rank integer NOT NULL,
    next_shelf_rank integer NOT NULL
);


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
)

CREATE TABLE IF NOT EXISTS public.distribution
(
    id bigserial PRIMARY KEY,
    name character varying(255),
    is_active boolean NOT NULL,
    rank integer NOT NULL,
    projection jsonb,
    connection jsonb,
    user_id bigint NOT NULL REFERENCES public.user(id),
    UNIQUE (user_id, name)
);



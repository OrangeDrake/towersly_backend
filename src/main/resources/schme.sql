
CREATE TABLE IF NOT EXISTS public.shelf
(
    id bigserial PRIMARY KEY,
    name character varying(255),
    is_active boolean NOT NULL,
    rank integer NOT NULL,
    next_work_rank integer NOT NULL,
    user_id bigint NOT NULL REFERENCES public.user(id)
);


CREATE TABLE IF NOT EXISTS public.user
(
    id bigint PRIMARY KEY,
    name character varying(255) UNIQUE,
    next_distribution_rank integer NOT NULL,
    next_shelf_rank integer NOT NULL
);

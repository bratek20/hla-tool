-- Autogenerated by HLA tool

CREATE TABLE some_dimension (
    some_dimension_id BIGINT DEFAULT NEXTVAL('common.the_sequence'::regclass) CONSTRAINT some_dimension_id PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    amount INTEGER NOT NULL,
    date_range jsonb NOT NULL
);

CREATE TABLE some_tracking_event (
   CONSTRAINT some_tracking_event_id PRIMARY KEY (event_id),
   some_dimension_id BIGINT NOT NULL
) INHERITS (event);
ALTER TYPE event_type ADD VALUE 'some_tracking_event';
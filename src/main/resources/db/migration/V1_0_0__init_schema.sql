CREATE TABLE users (
    id bigserial primary key,
    username varchar(50) not null,
    email varchar(32) unique not null,
    password varchar(50) not null
);
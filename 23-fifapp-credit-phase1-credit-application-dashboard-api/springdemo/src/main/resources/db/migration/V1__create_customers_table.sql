create table customers(
    id bigserial primary key,
    full_name varchar(100) not null,
    phone_number varchar(20) not null,
    email varchar(100),
    created_at timestamp not null default current_timestamp
    );
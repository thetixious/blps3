
create table cards
(
    credit_limit float(53),
    id           bigserial not null,
    bonus        varchar(255) check (bonus in ('LOW_PRICE', 'INTEREST_BALANCE')),
    goal         varchar(255) check (goal in ('PURCHASE', 'CASH', 'BONUS')),
    name         varchar(255),
    type         varchar(255) check (type in ('CREDIT', 'DEBIT')),
    primary key (id)
);
create table credit_offer
(
    approved     boolean,
    credit_limit float(53),
    ready        boolean,
    id           bigserial not null,
    userId       bigint,
    bonus        varchar(255) check (bonus in ('LOW_PRICE', 'INTEREST_BALANCE')),
    goal         varchar(255) check (goal in ('PURCHASE', 'CASH', 'BONUS')),
    primary key (id)
);
create table credit_offer_cards
(
    card_id         bigint not null,
    credit_offer_id bigint not null,
    primary key (card_id, credit_offer_id)
);
create table credit_offer_preferred_cards
(
    card_id         bigint not null,
    credit_offer_id bigint not null,
    primary key (card_id, credit_offer_id)
);
create table debit_offer
(
    id     bigserial not null,
    userId bigint,
    bonus  varchar(255) check (bonus in ('LOW_PRICE', 'INTEREST_BALANCE')),
    goal   varchar(255) check (goal in ('PURCHASE', 'CASH', 'BONUS')),
    primary key (id)
);
create table my_user
(
    is_fill  boolean,
    role     smallint check (role between 0 and 1),
    salary   float(53),
    id       bigserial not null,
    email    varchar(255),
    name     varchar(255),
    passport varchar(255),
    password varchar(255),
    surname  varchar(255),
    username varchar(255),
    primary key (id)
);
alter table if exists credit_offer
    add constraint FKsrv3nbmmeyrype4wao7qw1m1i foreign key (userId) references my_user;
alter table if exists credit_offer_cards
    add constraint FKdq76yaxy7c6ebii9upvo5w915 foreign key (card_id) references cards;
alter table if exists credit_offer_cards
    add constraint FKo9hicplu44mq0a2ynek64k0fb foreign key (credit_offer_id) references credit_offer;
alter table if exists credit_offer_preferred_cards
    add constraint FK5fkvebse66414bcma4r6qpc8x foreign key (card_id) references cards;
alter table if exists credit_offer_preferred_cards
    add constraint FK4hh3ptp5b5ri0jqvtv8qugrll foreign key (credit_offer_id) references credit_offer;
alter table if exists debit_offer
    add constraint FKlahky2xl9ng97f7ytsx22f70a foreign key (userId) references my_user;

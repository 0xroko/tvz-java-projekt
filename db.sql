drop table if exists item_on_order;
drop table if exists item;
drop table if exists category;
drop table if exists `table`;
drop table if exists `order`;

create table category
(
    id          int          not null auto_increment,
    name        varchar(255) not null,
    description varchar(255) not null,

    constraint category_pk
        primary key (id)
);

create table item
(
    id                      int            not null auto_increment,
    name                    varchar(255)   not null,
    description             varchar(255)   not null,
    price                   decimal(10, 2) not null,
    category_id             int            not null,
    stock                   int            not null,
    item_type               int            not null,
    default_stock_increment int            not null,
    preparation_time        time           null,
--         maybe some day...
    image                   varchar(255),
    constraint item_pk
        primary key (id)
);

create table `table`
(
    id          int          not null auto_increment,
    name        varchar(255) not null,
    description varchar(255) not null,
    seats       int          not null,

    constraint table_pk
        primary key (id)
);

alter table item
    add constraint item_category_id_fk
        foreign key (category_id) references category (id);

create index item_category_id_fk on item (category_id);

insert into category (id, name, description)
values (0, 'Burger', 'Burger'),
       (1, 'Pizza', 'Pizza'),
       (2, 'Pasta', 'Pasta'),
       (3, 'Salad', 'Salad'),
       (4, 'Dessert', 'Dessert'),
       (5, 'Drink', 'Drink'),
       (6, 'Side', 'Side'),
       (7, 'Combo', 'Combo'),
       (8, 'Sauce', 'Sauce'),
       (9, 'Extra', 'Extra'),
       (10, 'Cocktail', 'Cocktail');

/*set all items that have category that is being deleted to */

insert into item (name, description, price, category_id, stock, item_type, default_stock_increment, preparation_time)
values ('Cheeseburger', 'Cheeseburger', 5.00, 1, 100, 1, 1, '00:05:00'),
       ('Hamburger', 'Hamburger', 4.00, 1, 100, 1, 1, '00:05:00'),
       ('Double Cheeseburger', 'Double Cheeseburger', 6.00, 1, 100, 1, 1, '00:05:00'),
       ('Double Hamburger', 'Double Hamburger', 5.00, 1, 100, 1, 1, '00:05:00'),
       ('Bacon Cheeseburger', 'Bacon Cheeseburger', 6.00, 1, 100, 1, 1, '00:05:00'),
       ('Bacon Hamburger', 'Bacon Hamburger', 5.00, 1, 100, 1, 1, '00:05:00'),
       ('Bacon Double Cheeseburger', 'Bacon Double Cheeseburger', 7.00, 1, 100, 1, 1, '00:05:00'),
       ('Bacon Double Hamburger', 'Bacon Double Hamburger', 6.00, 1, 100, 1, 1, '00:05:00'),
       ('Cheeseburger Combo', 'Cheeseburger Combo', 7.00, 1, 100, 1, 1, '00:05:00'),
       ('Hamburger Combo', 'Hamburger Combo', 6.00, 1, 100, 1, 1, '00:05:00'),
       ('Double Cheeseburger Combo', 'Double Cheeseburger Combo', 8.00, 1, 100, 1, 1, '00:05:00'),
       ('Double Hamburger Combo', 'Double Hamburger Combo', 7.00, 1, 100, 1, 1, '00:05:00'),
       ('Bacon Cheeseburger Combo', 'Bacon Cheeseburger Combo', 8.00, 1, 100, 1, 1, '00:05:00'),
       ('Bacon Hamburger Combo', 'Bacon Hamburger Combo', 7.00, 1, 100, 1, 1, '00:05:00'),
--      few drinks (don't require prepare, 0 item type)
       ('Coca Cola', 'Coca Cola', 1.00, 5, 100, 0, 1, null),
       ('Fanta', 'Fanta', 1.00, 5, 100, 0, 1, null),
       ('Sprite', 'Sprite', 1.00, 5, 100, 0, 1, null),
       ('Water', 'Water', 1.00, 5, 100, 0, 1, null),
       ('Beer', 'Beer', 2.00, 5, 100, 0, 1, null),
       ('Wine', 'Wine', 3.00, 5, 100, 0, 1, null),
       ('Mojito', 'Mojito', 4.00, 10, 100, 0, 1, null),
       ('Caipirinha', 'Caipirinha', 4.00, 10, 100, 0, 1, null),
       ('Margarita', 'Margarita', 4.00, 10, 100, 0, 1, null),
       ('Mai Tai', 'Mai Tai', 4.00, 10, 100, 0, 1, null),
       ('Long Island Iced Tea', 'Long Island Iced Tea', 4.00, 10, 100, 0, 1, null),
       ('Bloody Mary', 'Bloody Mary', 4.00, 10, 100, 0, 1, null),
       ('Martini', 'Martini', 4.00, 10, 100, 0, 1, null),
       ('Cosmopolitan', 'Cosmopolitan', 4.00, 10, 100, 0, 1, null),
       ('Gin and Tonic', 'Gin and Tonic', 4.00, 10, 100, 0, 1, null),
       ('Screwdriver', 'Screwdriver', 4.00, 10, 100, 0, 1, null);

insert into `table` (name, description, seats)
values ('Table 1', 'Terasa dolje', 4),
       ('Table 2', 'Terasa dolje 2', 4),
       ('Table 3', 'Terasa gore', 4),
       ('Table 4', 'Terasa gore 2', 4),
       ('Table 5', 'Terasa gore 3', 4),
       ('Table 6', 'Terasa gore 4', 4),
       ('Table 7', 'Terasa gore 5', 4),
       ('Table 8', 'Terasa gore 6', 4);



create table `order`
(
    id         int          not null auto_increment,
    table_id   int          not null,
    user_id    int          not null,
    status     int          not null,
    order_time timestamp    not null default CURRENT_TIMESTAMP,

    note       varchar(255) null,
/*    created_at timestamp not null,
    updated_at timestamp not null,*/
    constraint order_pk
        primary key (id),
    constraint order_table_id_fk
        foreign key (table_id) references `table` (id)


);

/* delete all order items on delete*/
create trigger order_item_delete
    before delete
    on `order`
    for each row
    delete
    from item_on_order
    where item_on_order.order_id = old.id;


create table item_on_order
(
    id         int       not null auto_increment,
    order_id   int       not null,
    item_id    int       not null,
    status     int       not null,
    start_time timestamp not null default CURRENT_TIMESTAMP,
    /*created_at timestamp not null,
    updated_at timestamp not null,*/
    constraint order_item_pk
        primary key (id),
    constraint order_item_order_id_fk
        foreign key (order_id) references `order` (id),
    constraint order_item_item_id_fk
        foreign key (item_id) references item (id)
);


insert into `order` (table_id, user_id, status, note)
values (1, 1, 0, 'Obitelj 1'),
       (2, 1, 0, 'Obitelj 2'),
       (3, 1, 0, 'Obitelj 3'),
       (4, 1, 0, 'Obitelj 2'),
       (1, 1, 0, 'Obitelj 2'),
       (1, 1, 0, 'Obitelj 3'),
       (1, 1, 0, 'Obitelj 4'),
       (8, 1, 0, 'Obitelj 5');
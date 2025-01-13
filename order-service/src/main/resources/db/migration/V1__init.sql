create table t_orders
(
    id  bigint(20) NOT NULL AUTO_INCREMENT,
    order_number varchar(255) default null,
    sku_code varchar(255),
    price decimal(19,2),
    quantity int(11),
    PRIMARY KEY (id)
);

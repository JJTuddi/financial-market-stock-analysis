create table stocks
(
    ts         bigint     null,
    stock_name varchar(8) not null,
    open       double     not null,
    close      double     not null,
    volume     bigint     not null,
    low        double     not null,
    high       double     not null
);

create index timestamp_index
    on stocks (ts);

create index stock_name_index
    on stocks (stock_name);

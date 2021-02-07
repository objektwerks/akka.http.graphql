drop table users if exists;
create table users (id int primary key auto_increment, name varchar(128) not null);
insert into users (name) values ("Fred Flintstone");
insert into users (name) values ("Barney Rebel");
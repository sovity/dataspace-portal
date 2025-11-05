alter table component drop constraint component_pkey;
update component set name = id;
update component set id = client_id;
alter table component add primary key (id, environment);

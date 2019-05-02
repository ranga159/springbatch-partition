CREATE schema inventory_schema;
CREATE TABLE inventory_schema.book (
  id serial not null,
  title VARCHAR(100) NOT NULL,
  author_id int not null,
  published_date timestamp NOT NULL,
  isbn int,
  created timestamp,
  created_by varchar(100),
  modified timestamp,
  modified_by varchar(100),
  PRIMARY KEY (id),
  UNIQUE (isbn)
);
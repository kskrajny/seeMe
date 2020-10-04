BEGIN;
CREATE TABLE groups(id varchar NOT NULL PRIMARY KEY, name varchar NOT NULL);
CREATE TABLE chats(ip varchar NOT NULL PRIMARY KEY, name varchar NOT NULL, password NOT NULL);
CREATE TABLE messages(ip varchar NOT NULL PRIMARY KEY, received date NOT NULL, content text NOT NULL);
COMMIT;
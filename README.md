# secure-app

###  #1 Building the System
###  #2 Implement REST Endpoints
###  #3 Create Spring Security Schema
```sql
create schema if not exists secureapp;
use secureapp;

create table if not exists users(
	username varchar(50) not null primary key,
	password varchar(100) not null,
	enabled boolean not null
);

create table if not exists authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username));
	create unique index ix_auth_username on authorities (username,authority
);

insert into users (username, password, enabled) values ('plainUser', '$2a$10$KxTc8SYbIB/IaXCWz6NA4ug1pkAYM/e.P.0YQFGE3Ua4FZ6Qf842a', true);
insert into users (username, password, enabled) values ('managerUser', '$2a$10$QPnaeWBWz1BdDglni2CLzO2YMeifVXtQDPgUOVNETTcj8cEGwqiym', true);
insert into users (username, password, enabled) values ('adminUser', '$2a$10$Hc878CPLJ4hOtwyzt6V7..LHtzhcR3zqcXOAPseY9QGg05ZxcsTR6', true);

insert into authorities (username, authority) values ('plainUser', 'ROLE_USER');
insert into authorities (username, authority) values ('managerUser', 'ROLE_USER');
insert into authorities (username, authority) values ('managerUser', 'ROLE_MANAGER');
insert into authorities (username, authority) values ('adminUser', 'ROLE_USER');
insert into authorities (username, authority) values ('adminUser', 'ROLE_MANAGER');
insert into authorities (username, authority) values ('adminUser', 'ROLE_ADMIN');
```
* Some items to note about this code:
  * The passwords are hashed using the BCrypt library. Storing plain text passwords in a database is security risk. BCrypt is the recommended password hashing algorithm for Spring Security.
  * The plaintext value of each password is password.
  * We'll see how to create BCrypt hashed passwords in the next section.
  * We have three users with the following roles:
    * plainUser : ROLE_USER
    * managerUser : ROLE_USER, ROLE_MANAGER
    * adminUser : ROLE_USER, ROLE_MANAGER, ROLE_ADMIN
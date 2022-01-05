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

###  #4 Configure Spring Security
##### 4.1 Configure DataBase Connection
```xml
spring.datasource.url: jdbc:mysql://localhost:3306/secureapp?useSSL=false
spring.datasource.username: root
spring.datasource.password: rootroot
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver
```

##### 4.2 Configure Authentication Manager
Now we'll configure Spring Security to use the database for authentication and authorization data.
Some items to note about the code:

* This is a configuration class and is marked with the @Configuration annotation.
    * This class extends `WebSecurityConfigurerAdapter`
    * We are autowiring the data source configured in the previous step.
    * As mentioned above, storing plain text passwords in the database is a security risk so we hash the passwords in this database using a PasswordEncoder. We have chosen to use the BCrypt password encoder for this application. This will cause Spring Security to apply the BCrypt hashing algorithm to incoming passwords before comparing them to the value in the database.
    * We use the supplied AuthenticationManagerBuilder to configure Spring Security to use the database schema created previously. We need to supply a data source, a quey to find users by username, a query to find authorities by username, and a password encoder.

###  #5 Applying Spring Security to Endpoints
Our final step is to apply Spring Security to the endpoints of our application.
We'll do this in five parts:
1. Enable web security.
2. Turn on HTTP Basic (username and password) authentication.
3. Apply security rules to the endpoints.
4. Setup the logout rules.
5. Configure the Cross Site Request Forgery protection.

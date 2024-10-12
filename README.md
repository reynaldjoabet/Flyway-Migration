# Flyway-Migration
`docker exec -it mysqldb bash`
`mysql -h 127.0.0.1 -P 3306 -u root -p`


mariadb -uroot -ppass
CREATE DATABASE MyAwesomeApp

To list all databases:

`SHOW DATABASES`;

To create a new database:
`CREATE DATABASE database_name;`

To select a database to work with:
`USE database_name;`

To list all tables in the currently selected database:

`SHOW TABLES;`

To exit the MySQL command line `EXIT;` `QUIT;`


# Postgres

Users, groups, and roles
Users, groups, and roles are the same thing in PostgreSQL, with the only difference being that users have permission to log in by default. The CREATE USER and CREATE GROUP statements are actually aliases for the CREATE ROLE statement.

![alt text](image.png)

To create a PostgreSQL user, use the following SQL statement:

`CREATE USER myuser WITH PASSWORD 'secret_passwd';`

You can also create a user with the following SQL statement:

`CREATE ROLE myuser WITH LOGIN PASSWORD 'secret_passwd';`

Both of these statements create the exact same user. This new user does not have any permissions other than the default permissions available to the `public` role. All new users and roles inherit permissions from the `public` role

## Public schema and public role
When a new database is created, PostgreSQL by default creates a schema named public and grants access on this schema to a backend role named `public`. All new users and roles are by default granted this `public` role, and therefore can create objects in the `public` schema.

[managing-postgresql-users-and-roles](https://aws.amazon.com/blogs/database/managing-postgresql-users-and-roles/#:%7E:text=Users%2C%20groups%2C%20and%20roles%20are,to%20log%20in%20by%20default.&text=The%20roles%20are%20used%20only,grant%20them%20all%20the%20permissions)


[managing-database-migrations-kotlin](https://alexn.org/blog/2023/05/02/managing-database-migrations-kotlin/)

PostgreSQL roles can be a single role, or they can function as a group of roles. A user is a role with the ability to log in (the role has the `LOGIN` attribute)

PostgreSQL uses various mechanisms to implement authentication, authorization, and object ownership within database clusters. Core among these is the concept of roles

### What are roles?
In PostgreSQL, a role is a grouping of a specific set of capabilities, permissions, and "owned" entities. Instead of having distinct concepts of "users" and "groups", PostgreSQL uses roles to represent both of these ideas. A role can correspond to an individual person in the real world, or it can operate as a group with certain access that other roles can become members of




```sh
#  connect to the docker container
psql -h 127.0.0.1 -p 5432 -U postgres 
postgres=# \du
                             List of roles
 Role name |                         Attributes                         
-----------+------------------------------------------------------------
 postgres  | Superuser, Create role, Create DB, Replication, Bypass RLS



```


the default role or user for postgres is postgres and its password is postgres


## Role Attributes

A database role can have a number of attributes that define its privileges and interact with the client authentication system.

- login privilege

Only roles that have the LOGIN attribute can be used as the initial role name for a database connection. A role with the LOGIN attribute can be considered the same as a “database user”. To create a role with login privilege, use either:

```sh
CREATE ROLE name LOGIN;
CREATE USER name;
```

(CREATE USER is equivalent to CREATE ROLE except that CREATE USER includes LOGIN by default, while CREATE ROLE does not.)

- superuser status

A database superuser bypasses all permission checks, except the right to log in. This is a dangerous privilege and should not be used carelessly; it is best to do most of your work as a role that is not a superuser. To create a new database superuser, use `CREATE ROLE name SUPERUSER`. You must do this as a role that is already a superuser.
- database creation
A role must be explicitly given permission to create databases (except for superusers, since those bypass all permission checks). To create such a role, use `CREATE ROLE name CREATEDB`.

- role creation
A role must be explicitly given permission to create more roles (except for superusers, since those bypass all permission checks). To create such a role, use `CREATE ROLE name CREATEROLE`
- initiating replication
A role must explicitly be given permission to initiate streaming replication (except for superusers, since those bypass all permission checks). A role used for streaming replication must have LOGIN permission as well. To create such a role, use `CREATE ROLE name REPLICATION LOGIN`

- password
A password is only significant if the client authentication method requires the user to supply a password when connecting to the database. The password and md5 authentication methods make use of passwords. Database passwords are separate from operating system passwords. Specify a password upon role creation with `CREATE ROLE name PASSWORD 'string'`.

- inheritance of privileges
A role inherits the privileges of roles it is a member of, by default. However, to create a role which does not inherit privileges by default, use `CREATE ROLE name NOINHERIT`. Alternatively, inheritance can be overridden for individual grants by using `WITH INHERIT TRUE or WITH INHERIT FALSE`.
- bypassing row-level security
A role must be explicitly given permission to bypass every row-level security (RLS) policy (except for superusers, since those bypass all permission checks). To create such a role, use `CREATE ROLE name BYPASSRLS` as a superuser.
- connection limit
Connection limit can specify how many concurrent connections a role can make. -1 (the default) means no limit. Specify connection limit upon role creation with `CREATE ROLE name CONNECTION LIMIT 'integer'`

A role's attributes can be modified after creation with ALTER ROLE.


[authentication-and-authorization/role-management](https://www.prisma.io/dataguide/postgresql/authentication-and-authorization/role-management)

```sh
postgres=# SELECT rolname FROM pg_roles;
           rolname           
-----------------------------
 pg_database_owner
 pg_read_all_data
 pg_write_all_data
 pg_monitor
 pg_read_all_settings
 pg_read_all_stats
 pg_stat_scan_tables
 pg_read_server_files
 pg_write_server_files
 pg_execute_server_program
 pg_signal_backend
 pg_checkpoint
 pg_use_reserved_connections
 pg_create_subscription
 postgres
(15 rows)

# just those capable of logging in:
postgres=# SELECT rolname FROM pg_roles WHERE rolcanlogin;
 rolname  
----------
 postgres
(1 row)

```

In order to bootstrap the database system, a freshly initialized system always contains one predefined login-capable role. This role is always a “superuser”, and it will have the same name as the operating system user that initialized the database cluster with initdb unless a different name is specified. This role is often named postgres. In order to create more roles you first have to connect as this initial role.

Every connection to the database server is made using the name of some particular role, and this role determines the initial access privileges for commands issued in that connection. The role name to use for a particular database connection is indicated by the client that is initiating the connection request in an application-specific fashion. For example, the `psql` program uses the `-U` command line option to indicate the role to connect as. Many applications assume the name of the current operating system user by default (including `createuser` and `psql`). Therefore it is often convenient to maintain a naming correspondence between roles and operating system users.

```sh
CREATE ROLE readonly_role;
#assign role to another
# myuser inherits the priviledges of readonly_role
GRANT readonly_role TO myuser;

```

```sh
POSTGRES_HOST=psql
POSTGRES_PORT=5432
POSTGRES_DB=bank
POSTGRES_USER=bank
POSTGRES_PASSWORD=password
```

When you try to connect to a postgres database, by default it makes use of  peer authentication, which uses the name of the user you are logged in as and tries to connect to postgres using that username as user or role

 `postgres` user created on host when postgres is install
you can use ` sudo -i -u postgres` to login into this user
 now if you type `psql` it connects to the `postgres database` using the login user(`postgres`)

 

 A single postgres server or instance can contain multiple databases.Cluster here means a collection of databases hosted together 
 Each database can have multiple application schemas eg employee schema, accounts schema etc

 Each schema can contain multiple tables, indices, views etc( different types of objects)

 Schema is a collection of objects- vews,tables, functions etc

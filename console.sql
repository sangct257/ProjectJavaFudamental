create database DuAnJavaFudamental;

create table Admin(
	id serial primary key,
	username varchar(50) not null unique,
	password varchar(255) not null
);
insert into Admin(username, password) values ('admin', '123456');
create table Student(
    id serial primary key,
    name varchar(100) not null,
    dob date not null ,
    email varchar(100) not null unique,
    sex boolean not null,
    phone varchar(20) default null,
    password varchar(255) not null,
    create_at date default now()
);
insert into Student(name, dob, email, sex, password,phone) values ('Dinh Duc Sang', '2003-07-25', 'sangct257003@gmail.com', true, '123456', '0987654321');
create table Course(
    id serial primary key,
    name varchar(100) not null,
    duration int not null,
    instructor varchar(100) not null,
    create_at date default now()
);

create type enrollment_status as enum ('WAITING', 'DENIED', 'CANCELED', 'CONFIRMED');

create table Enrollment(
    id serial primary key,
    student_id int references Student(id) not null ,
    course_id int references Course(id) not null,
    registered_at timestamp default now(),
    status enrollment_status default 'WAITING'
)
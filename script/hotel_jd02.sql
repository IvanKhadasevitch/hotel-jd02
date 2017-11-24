drop database hotel_jd02;

create database hotel_jd02;

show databases;

use hotel_jd02;

create table `USER` (
	id 			bigint not null auto_increment,
    birthDate 	date not null,
    email 		varchar(64) not null unique,
   `name` 		varchar(20) not null,
   `password` 	varchar(70) not null,
    surname 	varchar(30) not null,
    version		 bigint,
    
    primary key (id)
) engine=InnoDB; 
#alter table `USER` add constraint UK_oso07pudw19e66bs4yp8hwpux unique (email);

describe `USER`;

create table HOTEL (
	id 		     bigint not null auto_increment,
    email		 varchar(64) not null,
   `name`		 varchar(30) not null unique,
	version		 bigint,
    
   primary key (id)
) engine=InnoDB; 
#alter table HOTEL add constraint UK_okrc3k06oq1htxup2iiva1y13 unique (name);
   
describe HOTEL;    

create table ADMIN (
	id 			 bigint not null auto_increment,
   `name`		 varchar(30) not null,
   `password`	 varchar(70) not null,
    version		 bigint,
    hotel_id	 bigint,
    
    primary key (id),
    constraint FK_T_ADMIN_F_HOTEL_ID_T_HOTEL_F_ID foreign key (hotel_id) references HOTEL (id) 
) engine=InnoDB; 
#alter table ADMIN add constraint FK3w20ee819pryho7qduba5smyh foreign key (hotel_id) references HOTEL (id); 
									
describe ADMIN;    

create table ROOM_TYPE (
	id 			bigint not null auto_increment,
    currency 	varchar(12) not null,
   `name`		varchar(30) not null,
    price 		integer not null,
    seats 		integer not null,
    version		bigint,
    hotel_id 	bigint,
    
    primary key (id),
    constraint FK_T_ROOM_TUPE_F_HOTEL_ID_T_HOTEL_F_ID foreign key (hotel_id) references HOTEL (id)
) engine=InnoDB; 
#alter table ROOM_TYPE add constraint FK1v2ovu1dlwlp1d9qiloldq5uv foreign key (hotel_id) references HOTEL (id); 

describe ROOM_TYPE;  

create table ROOM (
	id 			 bigint not null auto_increment,
   `number` 	 varchar(10),
    version		 bigint,
    roomType_id	 bigint, 
    
	primary key (id),
    constraint FK_T_ROOM_F_ROOMTYPE_ID_T_ROOM_TYPE_F_ID foreign key (roomType_id) references ROOM_TYPE (id)
) engine=InnoDB; 
#alter table ROOM add constraint FKdotq2i2j3p8vkm6fmpiwg6rb4 foreign key (roomType_id) references ROOM_TYPE (id); 

describe ROOM;  

create table `ORDER` (
	id 				bigint not null auto_increment,
    arrivalDate 	date,
    eventsDate 		date,
    createDate 		date,
	updateDate 		date,
   `status`			varchar(12) not null,
    total			bigint,
    version 		bigint,
    admin_id 		bigint,
    bill_id 		bigint,
    room_id 		bigint,
   `user_id` 		bigint,
    
    primary key (id),
    constraint FK_T_ORDER_F_ADMIN_ID_T_ADMIN_F_ID foreign key (admin_id) references ADMIN (id),
#   constraint FK_T_ORDER_F_BILL_ID_T_BILL_F_ID foreign key (bill_id) references BILL (id),
    constraint FK_T_ORDER_F_ROOM_ID_T_ROOM_F_ID foreign key (room_id) references ROOM (id),
	constraint FK_T_ORDER_F_USER_ID_T_USER_F_ID foreign key (`user_id`) references `USER` (id)
) engine=InnoDB;
#alter table `ORDER` add constraint FK64966l9tf4039p9b08gv7rk27 foreign key (admin_id) references ADMIN (id);
#alter table `ORDER` add constraint FKst9p8xoj4pupwy8r5nveeyk3w foreign key (bill_id) references BILL (id);
#alter table `ORDER` add constraint FKfbac3h609shfroqkk19s7n62d foreign key (room_id) references ROOM (id);
#alter table `ORDER` add constraint FKngdjtfs41hu1ygwnsonntiwgd foreign key (`user_id`) references `USER` (id);
 

create table BILL (
	id 					bigint not null auto_increment,
    arrivalDate			date,
    eventsDate 			date,
	createDate 			date,
   `status` 			varchar(12) not null,
    total 				bigint,
    version				bigint,
   `order_id` 			bigint,
    room_id 			bigint,
   `user_id` 			bigint,
    
    primary key (id),
#	constraint FK_T_BILL_F_ORDER_ID_T_ORDER_F_ID foreign key (`order_id`) references `ORDER` (id),
	constraint FK_T_BILL_F_ROOM_ID_T_ROOM_F_ID foreign key (room_id) references ROOM (id),
	constraint FK_T_BILL_F_USER_ID_T_USER_F_ID foreign key (`user_id`) references `USER` (id)     
) engine=InnoDB;
#alter table BILL add constraint FKmd9ka45igtft1g70cmpl8tm71 foreign key (`order_id`) references `ORDER` (id);
#alter table BILL add constraint FK9ujqlk0dosd9y5pqai4lhharx foreign key (room_id) references ROOM (id);
#alter table BILL add constraint FKcpjj3vp7t41fn12rj0ebuc3ib foreign key (`user_id`) references `USER` (id);    

alter table `ORDER` add constraint FK_T_ORDER_F_BILL_ID_T_BILL_F_ID foreign key (bill_id) references BILL (id);
alter table BILL add constraint FK_T_BILL_F_ORDER_ID_T_ORDER_F_ID foreign key (`order_id`) references `ORDER` (id);

describe BILL;  
describe `ORDER`; 

							#--------------INIT DATA-------------

#---------------------------------------TABLE USER--------------------
insert into `USER` set 
    `name`			= 'Maikle',
    surname			= 'Jordan',
    birthDate 		= '1980-10-12',
    email			= 'jordan@gmail.com',
    `password`		= '1';
insert into `USER` set 
    `name`			= 'Alexandre',
    surname			= 'Pushkin',
    birthDate		= '1799-06-06',
    email			= 'pushkin@mail.ru',
    `password`		= '2';
insert into `USER` set 
    `name`			= 'Vasja',
    surname			= 'Pypkin',
    birthDate	 	= '1970-01-13',
    email			= 'pypkin@yandex.ru',
    `password`		= '3';
insert into `USER` set 
    `name`			= 'Marija',
    surname			= 'Pobeda',
    birthDate		= '1945-05-09',
    email			= 'pobeda@gmail.com', 
    `password`		= '4';
select * from  `USER`;  
  
#---------------------------------------TABLE HOTEL--------------------    
insert into HOTEL set 
    `name`			= 'Negresko Princes',
    email			= 'negresko@gmail.com';  
insert into HOTEL set 
    `name`			= 'Royal luxury resort',
    email			= 'royalLuxuryResort@gmail.com';     
insert into HOTEL set 
    `name`			= 'Nalchik Rufik Nostalgia',
    email			= 'rufikNostalgia@gmail.com';      
    
select * from  hotel;  

#---------------------------------------TABLE ROOM_TYPE-------------------- 

									#---- HOTEL set  `name`	= 'Negresko Princes',
insert into ROOM_TYPE set 
    `name`			= 'ONE',
    seats			= 1,  
    price			= 3000,
    currency 		= 'USD',
    hotel_id		= 1;  
insert into ROOM_TYPE set 
    `name`			= 'DOUBLE',
    seats			= 2,  
    price			= 5000,
    currency 		= 'USD',
    hotel_id		= 1; 
insert into ROOM_TYPE set 
    `name`			= 'DOUBLE SUITE',
    seats			= 2,  
    price			= 7000,
    currency 		= 'USD',
    hotel_id		= 1; 
insert into ROOM_TYPE set 
    `name`			= 'FOUR FAMILY',
    seats			= 4,  
    price			= 10000,
    currency 		= 'USD',
    hotel_id		= 1; 
insert into ROOM_TYPE set 
    `name`			= 'FOUR SUITE',
    seats			= 4,  
    price			= 15000,
    currency 		= 'USD',
    hotel_id		= 1;     
										#----HOTEL set `name`			= 'Royal luxury resort',
insert into ROOM_TYPE set 
    `name`			= 'ROYAL SUITE',
    seats			= 7,  
    price			= 150000,
    currency 		= 'USD',
    hotel_id		= 2;   
    
insert into ROOM_TYPE set 
    `name`			= 'PRESIDENT SUITE',
    seats			= 7,  
    price			= 300000,
    currency 		= 'USD',
    hotel_id		= 2;     
    
										#----HOTEL set `name`			= 'Nalchik Rufik Nostalgia',
insert into ROOM_TYPE set 
    `name`			= 'MAGIC MONSVARD',
    seats			= 5,  
    price			= 10000,
    currency 		= 'EURO',
    hotel_id		= 3;   
    
insert into ROOM_TYPE set 
    `name`			= 'ALIBABAS LUXURY CAVE',
    seats			= 12,  
    price			= 10000,
    currency 		= 'EURO',
    hotel_id		= 3;     
     
select * from  ROOM_TYPE; 

#---------------------------------------TABLE ROOM-------------------- 

										#---- HOTEL = 'Negresko Princes',
                                        #--- ROOM_TYPE `name`	= 'ONE',
insert into ROOM set 
    `number`			= '101',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '102',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '103',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '104',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '105',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '106',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '107',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '108',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '109',
    roomType_id			= 1;
insert into ROOM set 
    `number`			= '110',
    roomType_id			= 1;
									#--- ROOM_TYPE 		= 'DOUBLE',
insert into ROOM set 
    `number`			= '201',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '202',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '203',
    roomType_id	= 2;
insert into ROOM set 
    `number`			= '204',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '205',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '206',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '207',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '208',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '209',
    roomType_id			= 2;
insert into ROOM set 
    `number`			= '210',
    roomType_id			= 2;
										#--- ROOM_TYPE 		= 'DOUBLE SUITE',
insert into ROOM set 
    `number`			= '301',
    roomType_id			= 3;  
insert into ROOM set 
    `number`			= '302',
    roomType_id			= 3;  
insert into ROOM set 
    `number`			= '303',
    roomType_id			= 3;  
insert into ROOM set 
    `number`			= '304',
    roomType_id			= 3;  
insert into ROOM set 
    `number`			= '305',
    roomType_id			= 3;  
insert into ROOM set 
    `number`			= '306',
    roomType_id			= 3;  
insert into ROOM set 
    `number`			= '307',
    roomType_id			= 3;      
									#--- ROOM_TYPE 		= 'FOUR FAMILY',
insert into ROOM set 
    `number`			= '401',
    roomType_id			= 4;  
insert into ROOM set 
    `number`			= '402',
    roomType_id			= 4;  
insert into ROOM set 
    `number`			= '403',
    roomType_id			= 4;  
insert into ROOM set 
    `number`			= '404',
    roomType_id			= 4;  
insert into ROOM set 
    `number`			= '405',
    roomType_id			= 4;  
								#--- ROOM_TYPE 		= 'FOUR SUITE',
insert into ROOM set 
    `number`			= '501',
    roomType_id			= 5;     
insert into ROOM set 
    `number`			= '502',
    roomType_id			= 5;  
insert into ROOM set 
    `number`			= '503',
    roomType_id			= 5;  
insert into ROOM set 
    `number`			= '504',
    roomType_id			= 5;  
insert into ROOM set 
    `number`			= '505',
    roomType_id			= 5;      

								#hotel_name			= 'Royal luxury resort'
                                #--- ROOM_TYPE 		= 'ROYAL SUITE',
insert into ROOM set 
    `number`			= '101',
    roomType_id			= 6;
insert into ROOM set 
    `number`			= '102',
    roomType_id			= 6;  
insert into ROOM set 
    `number`			= '103',
    roomType_id			= 6;
insert into ROOM set 
    `number`			= '104',
    roomType_id			= 6;
insert into ROOM set 
    `number`			= '105',
    roomType_id			= 6;  
								#--- ROOM_TYPE 		= 'PRESIDENT SUITE',
insert into ROOM set 
    `number`			= '201',
    roomType_id			= 7;    
insert into ROOM set 
    `number`			= '202',
    roomType_id			= 7;  
insert into ROOM set 
    `number`			= '203',
    roomType_id			= 7;  
insert into ROOM set 
    `number`			= '204',
    roomType_id			= 7;  
insert into ROOM set 
    `number`			= '205',
    roomType_id			= 7;      
								#hotel_name			= 'Nalchik Rufik Nostalgia'
                                #--- ROOM_TYPE 		= 'MAGIC MONSVARD',
insert into ROOM set 
    `number`			= '101',
    roomType_id			= 8;
insert into ROOM set 
    `number`			= '102',
    roomType_id			= 8;  
insert into ROOM set 
    `number`			= '103',
    roomType_id			= 8;
insert into ROOM set 
    `number`			= '104',
    roomType_id			= 8;
insert into ROOM set 
    `number`			= '105',
    roomType_id			= 8;  
insert into ROOM set 
    `number`			= '106',
    roomType_id			= 8; 
insert into ROOM set 
    `number`			= '107',
    roomType_id			= 8;   
                                #--- ROOM_TYPE 		= ALIBABAS LUXURY CAVE,
insert into ROOM set 
    `number`			= '101',
    roomType_id			= 9;
insert into ROOM set 
    `number`			= '102',
    roomType_id			= 9;  
insert into ROOM set 
    `number`			= '103',
    roomType_id			= 9;
insert into ROOM set 
    `number`			= '104',
    roomType_id			= 9;
insert into ROOM set 
    `number`			= '105',
    roomType_id			= 9;  
insert into ROOM set 
    `number`			= '106',
    roomType_id			= 9; 
insert into ROOM set 
    `number`			= '107',
    roomType_id			= 9;     

select * from  ROOM; 

#---------------------------------------TABLE ADMIN-------------------- 
insert into ADMIN set 
    hotel_id		= '1',
    `name`			= 'Cool Admin negro',
    `password` 		= 'xMpCOKC5I4INzFCab3WEmw==';
insert into ADMIN set 
    hotel_id		= '1',
    `name`			= 'Пчелка Жу. Отель Негреско',
    `password` 		= 'xMpCOKC5I4INzFCab3WEmw==';    
insert into ADMIN set 
    hotel_id		= '2',
    `name`			= 'Cool Admin royal',
    `password` 		= 'yB5yjZ1ML2NvBn+JzBSGLA==';    
insert into ADMIN set 
    hotel_id		= '2',
    `name`			= 'Трудяга Вася. Отель Роял',
    `password` 		= 'yB5yjZ1ML2NvBn+JzBSGLA=='; 
insert into ADMIN set 
    hotel_id		= '3',
    `name`			= 'Rafik Admin Nalchik',
    `password` 		= '7MvIfktc4v4oMI/Z8qe68w==';     
    
select * from  ADMIN;     
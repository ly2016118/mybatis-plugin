create table t_user (
   id                 SERIAL               not null,
   phone         VARCHAR(200)          null,
   name          VARCHAR(30)          null,

   constraint PK_T_user primary key (id)
);

create table t_address (
   id                 SERIAL               not null,
   province         VARCHAR(64)          null,
   city          VARCHAR(64)          null,
   
   constraint PK_T_address primary key (id)
);


create table t_house (
   id                 SERIAL               not null,
   address         VARCHAR(64)          null,
   color          VARCHAR(64)          null,

   constraint PK_T_house primary key (id)
);


create table t_phone (
   id                 SERIAL               not null,
   user_id         INTEGER         null,
   phone          VARCHAR(200)          null,

   constraint PK_T_phone primary key (id)
);



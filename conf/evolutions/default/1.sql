# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table activity (
  id                        bigint auto_increment not null,
  user_id                   bigint not null,
  kind                      varchar(255),
  location                  varchar(255),
  distance                  double,
  start_time                varchar(255),
  duration                  varchar(255),
  constraint pk_activity primary key (id))
;

create table friends (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  friend_id                 bigint,
  accepted                  varchar(255),
  constraint pk_friends primary key (id))
;

create table location (
  id                        bigint auto_increment not null,
  activity_id               bigint not null,
  latitude                  float,
  longitude                 float,
  constraint pk_location primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  firstname                 varchar(255),
  lastname                  varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  is_public_viewable        tinyint(1) default 0,
  is_friend_viewable        tinyint(1) default 0,
  profile_photo             varchar(255),
  last_login_date           datetime,
  constraint pk_user primary key (id))
;

alter table activity add constraint fk_activity_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_activity_user_1 on activity (user_id);
alter table location add constraint fk_location_activity_2 foreign key (activity_id) references activity (id) on delete restrict on update restrict;
create index ix_location_activity_2 on location (activity_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table activity;

drop table friends;

drop table location;

drop table user;

SET FOREIGN_KEY_CHECKS=1;


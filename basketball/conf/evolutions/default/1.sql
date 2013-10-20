# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table box_score (
  id                        bigint not null,
  team_id                   bigint,
  game_id                   bigint,
  location                  varchar(5) not null,
  result                    varchar(4),
  points                    smallint,
  assists                   smallint,
  turnovers                 smallint,
  steals                    smallint,
  blocks                    smallint,
  fieldGoalAttempts         smallint,
  fieldGoalMade             smallint,
  fieldGoalPercent          float,
  threePointAttempts        smallint,
  threePointMade            smallint,
  threePointPercent         float,
  freeThrowAttempts         smallint,
  freeThrowMade             smallint,
  freeThrowPercent          float,
  reboundsOffense           smallint,
  reboundsDefense           smallint,
  personalFouls             smallint,
  constraint ck_box_score_location check (location in ('Away','Home')),
  constraint ck_box_score_result check (result in ('Loss','Win')),
  constraint pk_box_score primary key (id))
;

create table game (
  id                        bigint not null,
  date                      timestamp not null,
  status                    varchar(9) not null,
  seasonType                varchar(7) not null,
  last_update               timestamp not null,
  constraint ck_game_status check (status in ('Scheduled','Cancelled','Postponed','Finished','Suspended','Completed')),
  constraint ck_game_seasonType check (seasonType in ('Post','Regular','Pre')),
  constraint pk_game primary key (id))
;

create table game_official (
  id                        bigint not null,
  game_id                   bigint,
  official_id               bigint,
  constraint pk_game_official primary key (id))
;

create table official (
  id                        bigint not null,
  number                    varchar(2) not null,
  lastName                  varchar(35) not null,
  firstName                 varchar(35) not null,
  firstGame                 timestamp not null,
  active                    boolean not null,
  last_update               timestamp not null,
  constraint pk_official primary key (id))
;

create table period_score (
  id                        bigint not null,
  boxscore_id               bigint,
  quarter                   smallint not null,
  score                     smallint not null,
  constraint pk_period_score primary key (id))
;

create table team (
  id                        bigint not null,
  team_key                  varchar(35) not null,
  full_name                 varchar(35) not null,
  short_name                varchar(20) not null,
  abbr                      varchar(5) not null,
  active                    boolean not null,
  conference                varchar(4) not null,
  division                  varchar(9) not null,
  site_name                 varchar(30) not null,
  city                      varchar(15) not null,
  state                     varchar(2) not null,
  last_update               timestamp not null,
  constraint ck_team_conference check (conference in ('West','East')),
  constraint ck_team_division check (division in ('Central','Atlantic','Northwest','Pacific','Southeast','Southwest')),
  constraint pk_team primary key (id))
;

create sequence box_score_seq;

create sequence game_seq;

create sequence game_official_seq;

create sequence official_seq;

create sequence period_score_seq;

create sequence team_seq;

alter table box_score add constraint fk_box_score_team_1 foreign key (team_id) references team (id) on delete restrict on update restrict;
create index ix_box_score_team_1 on box_score (team_id);
alter table box_score add constraint fk_box_score_game_2 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_box_score_game_2 on box_score (game_id);
alter table game_official add constraint fk_game_official_game_3 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_game_official_game_3 on game_official (game_id);
alter table game_official add constraint fk_game_official_official_4 foreign key (official_id) references official (id) on delete restrict on update restrict;
create index ix_game_official_official_4 on game_official (official_id);
alter table period_score add constraint fk_period_score_boxScore_5 foreign key (boxscore_id) references box_score (id) on delete restrict on update restrict;
create index ix_period_score_boxScore_5 on period_score (boxscore_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists box_score;

drop table if exists game;

drop table if exists game_official;

drop table if exists official;

drop table if exists period_score;

drop table if exists team;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists box_score_seq;

drop sequence if exists game_seq;

drop sequence if exists game_official_seq;

drop sequence if exists official_seq;

drop sequence if exists period_score_seq;

drop sequence if exists team_seq;


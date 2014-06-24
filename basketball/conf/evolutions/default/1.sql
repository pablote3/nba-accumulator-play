# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table box_score (
  id                        bigint auto_increment not null,
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

create table box_score_player (
  id                        bigint auto_increment not null,
  boxScore_id               bigint,
  rosterPlayer_id           bigint,
  position                  varchar(5) not null,
  minutes                   smallint,
  starter                   tinyint(1) default 0 not null,
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
  constraint ck_box_score_player_position check (position in ('F','SF','G','SG','C','PF','PG')),
  constraint pk_box_score_player primary key (id))
;

create table game (
  id                        bigint auto_increment not null,
  date                      datetime not null,
  status                    varchar(9) not null,
  seasonType                varchar(7) not null,
  last_update               datetime not null,
  constraint ck_game_status check (status in ('Scheduled','Cancelled','Postponed','Finished','Suspended','Completed')),
  constraint ck_game_seasonType check (seasonType in ('Post','Regular','Pre')),
  constraint pk_game primary key (id))
;

create table game_official (
  id                        bigint auto_increment not null,
  game_id                   bigint,
  official_id               bigint,
  constraint pk_game_official primary key (id))
;

create table official (
  id                        bigint auto_increment not null,
  number                    varchar(2) not null,
  lastName                  varchar(35) not null,
  firstName                 varchar(35) not null,
  firstGame                 date not null,
  active                    tinyint(1) default 0 not null,
  last_update               datetime not null,
  constraint pk_official primary key (id))
;

create table period_score (
  id                        bigint auto_increment not null,
  boxscore_id               bigint,
  quarter                   smallint not null,
  score                     smallint not null,
  constraint pk_period_score primary key (id))
;

create table player (
  id                        bigint auto_increment not null,
  last_name                 varchar(25) not null,
  first_name                varchar(25) not null,
  display_name              varchar(50) not null,
  height                    smallint,
  weight                    smallint,
  birthdate                 date,
  birthplace                varchar(50),
  last_update               datetime not null,
  constraint pk_player primary key (id))
;

create table roster_player (
  id                        bigint auto_increment not null,
  team_id                   bigint,
  player_id                 bigint,
  fromDate                  date not null,
  toDate                    date not null,
  position                  varchar(5) not null,
  number                    varchar(2),
  constraint ck_roster_player_position check (position in ('F','SF','G','SG','C','PF','PG')),
  constraint pk_roster_player primary key (id))
;

create table team (
  id                        bigint auto_increment not null,
  team_key                  varchar(35) not null,
  full_name                 varchar(35) not null,
  short_name                varchar(20) not null,
  abbr                      varchar(5) not null,
  active                    tinyint(1) default 0 not null,
  conference                varchar(4) not null,
  division                  varchar(9) not null,
  site_name                 varchar(30) not null,
  city                      varchar(15) not null,
  state                     varchar(2) not null,
  last_update               datetime not null,
  constraint ck_team_conference check (conference in ('West','East')),
  constraint ck_team_division check (division in ('Central','Atlantic','Northwest','Pacific','Southeast','Southwest')),
  constraint pk_team primary key (id))
;

alter table box_score add constraint fk_box_score_team_1 foreign key (team_id) references team (id) on delete restrict on update restrict;
create index ix_box_score_team_1 on box_score (team_id);
alter table box_score add constraint fk_box_score_game_2 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_box_score_game_2 on box_score (game_id);
alter table box_score_player add constraint fk_box_score_player_boxScore_3 foreign key (boxScore_id) references box_score (id) on delete restrict on update restrict;
create index ix_box_score_player_boxScore_3 on box_score_player (boxScore_id);
alter table box_score_player add constraint fk_box_score_player_rosterPlayer_4 foreign key (rosterPlayer_id) references roster_player (id) on delete restrict on update restrict;
create index ix_box_score_player_rosterPlayer_4 on box_score_player (rosterPlayer_id);
alter table game_official add constraint fk_game_official_game_5 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_game_official_game_5 on game_official (game_id);
alter table game_official add constraint fk_game_official_official_6 foreign key (official_id) references official (id) on delete restrict on update restrict;
create index ix_game_official_official_6 on game_official (official_id);
alter table period_score add constraint fk_period_score_boxScore_7 foreign key (boxscore_id) references box_score (id) on delete restrict on update restrict;
create index ix_period_score_boxScore_7 on period_score (boxscore_id);
alter table roster_player add constraint fk_roster_player_team_8 foreign key (team_id) references team (id) on delete restrict on update restrict;
create index ix_roster_player_team_8 on roster_player (team_id);
alter table roster_player add constraint fk_roster_player_player_9 foreign key (player_id) references player (id) on delete restrict on update restrict;
create index ix_roster_player_player_9 on roster_player (player_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table box_score;

drop table box_score_player;

drop table game;

drop table game_official;

drop table official;

drop table period_score;

drop table player;

drop table roster_player;

drop table team;

SET FOREIGN_KEY_CHECKS=1;


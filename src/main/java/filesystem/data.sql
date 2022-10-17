create database if not exists `file_info`;
use file_info;
create table directory (
  path varchar(255) not null,
  subdirectories int(100) not null,
  files int(100) not null,
  size_bytes varchar(100) not null,
  size int(100) not null,
  sync_time timestamp not null default current_timestamp,
  version int(100) not null,
);
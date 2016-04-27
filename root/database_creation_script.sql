CREATE DATABASE `nildb`;

CREATE USER 'nildb'@'localhost' IDENTIFIED BY 'nildb2016';

CREATE TABLE `nildb`.`Users` ( 
	`user_id` VARCHAR(20)  NOT NULL COMMENT 'The name attached to this user account entry' , 
	`password` VARCHAR(200) NOT NULL COMMENT 'This user account''s password' , 
	`is_admin` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether this user is an site administrator' , 
	PRIMARY KEY (`user_id`)
) 
ENGINE = InnoDB COMMENT = 'A collection of user accounts';


CREATE TABLE `nildb`.`Access_token` (
	`access_token` VARCHAR(60) NOT NULL COMMENT 'The access token granted for a particular user',
	`user_id` VARCHAR(20)  NOT NULL COMMENT 'The name attached to this user account entry' , 
	`password` VARCHAR(60) NOT NULL COMMENT 'This user account''s password' , 
	`is_admin` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether this user is an site administrator' , 
	PRIMARY KEY (`access_token`)
) 
ENGINE = InnoDB COMMENT = 'A collection of access token granted to users';


CREATE TABLE nildb.Organizations (
	org_id VARCHAR(6) NOT NULL COMMENT 'The unique id for the organization',
	org_name VARCHAR(60) NOT NULL COMMENT 'The Full Name of the organization',
	PRIMARY KEY (org_id)
)
ENGINE = InnoDB;

CREATE TABLE `nildb`.`Players` ( 
	`player_id` SERIAL,
	`player_name` VARCHAR(30) NOT NULL COMMENT 'The unique player id that allows access to games & indexes play data',
	`birthdate` DATE DEFAULT NULL COMMENT 'The birthdate if the player with this player id',
	`gender` VARCHAR(15) NOT NULL COMMENT 'The gender of this player',
	`active` BOOLEAN NOT NULL COMMENT 'Determines if this player is allowed to log in and play games.',
	`org_id` VARCHAR(6) NOT NULL COMMENT 'The oranization this player belongs to',
	PRIMARY KEY(`player_id`),
	CONSTRAINT FOREIGN KEY(`org_id`) REFERENCES `nildb`.`Organizations` (`org_id`),
	UNIQUE( `org_id`,`player_name`)
)
ENGINE = InnoDB;

CREATE TABLE `nildb`.`Assessments` 
(
	`player_id` BIGINT(20) UNSIGNED,
	`assessment_name` VARCHAR(30),
	`value` VARCHAR(10),
	`date_and_time` DATETIME,
	PRIMARY KEY ( `player_id`,`assessment_name`,`value`,`date_and_time`),
	CONSTRAINT FOREIGN KEY (`player_id`) REFERENCES `nildb`.`Players` (`player_id`)
)
ENGINE = InnoDB;

CREATE TABLE `nildb`.`Org_User` (
	`org_id` VARCHAR(6) NOT NULL COMMENT 'The organization the related user is able to access',
	`user_id` VARCHAR(20) NOT NULL COMMENT 'The user that is being given access to the org',
 	CONSTRAINT FOREIGN KEY(`org_id`) REFERENCES `nildb`.`Organizations` (`org_id`),
 	CONSTRAINT FOREIGN KEY(`user_id`) REFERENCES `nildb`.`Users` (`user_id`),
 	PRIMARY KEY (`user_id`, `org_id`)
)
ENGINE = InnoDB;


GRANT SELECT, INSERT, EXECUTE, DELETE, TRIGGER, UPDATE ON nildb.* TO 'nildb'@'localhost';

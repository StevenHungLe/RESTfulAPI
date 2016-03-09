CREATE DATABASE `mydb`;

# "CREATE USER 'recess'@'localhost' IDENTIFIED BY 'recess5313538755432435548787987133121387654362';"
CREATE USER 'hungle'@'localhost' IDENTIFIED BY 'hungle123456';

CREATE TABLE `mydb`.`Users` ( 
	`user_id` VARCHAR(20)  NOT NULL COMMENT 'The name attached to this user account entry' , 
	`password` VARCHAR(60) NOT NULL COMMENT 'This user account''s password' , 
	`is_admin` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether this user is an site administrator' , 
	PRIMARY KEY (`user_id`)
) 
ENGINE = InnoDB COMMENT = 'A collection of user accounts';

INSERT INTO `mydb`.`Users` (`user_id`,`password`,`is_admin`)
VALUES ('hungle', "hungle123456", TRUE );


CREATE TABLE `mydb`.`Access_token` (
	`access_token` VARCHAR(60) NOT NULL COMMENT 'The access token granted for a particular user',
	`user_id` VARCHAR(20)  NOT NULL COMMENT 'The name attached to this user account entry' , 
	`password` VARCHAR(60) NOT NULL COMMENT 'This user account''s password' , 
	`is_admin` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether this user is an site administrator' , 
	PRIMARY KEY (`access_token`)
) 
ENGINE = InnoDB COMMENT = 'A collection of access token granted to users';


CREATE TABLE mydb.Organizations (
	org_id VARCHAR(6) NOT NULL COMMENT 'The unique id for the organization',
	org_name VARCHAR(60) NOT NULL COMMENT 'The Full Name of the organization',
	PRIMARY KEY (org_id)
)
ENGINE = InnoDB;

CREATE TABLE `mydb`.`Players` ( 
	`id` SERIAL,
	`player_name` VARCHAR(30) NOT NULL COMMENT 'The unique player id that allows access to games & indexes play data',
	`birthdate` DATE DEFAULT NULL COMMENT 'The birthdate if the player with this player id',
	`gender` VARCHAR(15) NOT NULL COMMENT 'The gender of this player',
	`active` BOOLEAN NOT NULL COMMENT 'Determines if this player is allowed to log in and play games.',
	`org_id` VARCHAR(6) NOT NULL COMMENT 'The oranization this player belongs to',
	PRIMARY KEY(`id`),
	CONSTRAINT FOREIGN KEY(`org_id`) REFERENCES `mydb`.`Organizations` (`org_id`),
	UNIQUE( `org_id`,`player_name`)
)
ENGINE = InnoDB;


CREATE TABLE `mydb`.`Org_User` (
	`org_id` VARCHAR(6) NOT NULL COMMENT 'The organization the related user is able to access',
	`usr_id` VARCHAR(20) NOT NULL COMMENT 'The user that is being given access to the org',
 	CONSTRAINT FOREIGN KEY(`org_id`) REFERENCES `mydb`.`Organizations` (`org_id`),
 	CONSTRAINT FOREIGN KEY(`usr_id`) REFERENCES `mydb`.`Users` (`user_id`),
 	PRIMARY KEY (`usr_id`, `org_id`)
)
ENGINE = InnoDB;


GRANT SELECT, INSERT, EXECUTE, DELETE, TRIGGER, UPDATE ON mydb.* TO 'hungle'@'localhost';

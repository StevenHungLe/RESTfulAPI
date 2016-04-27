/* This file is to be used to create test data for the 
	'nildb' database 
*/ 

/* User accounts */
INSERT INTO nildb.Users 
   ( user_id, password, is_admin )
	VALUES 
	( "admin", SHA2("admin123456",512), TRUE ),  /* Insert admin user */
	( "1", SHA2("1",512), TRUE), /* admin user */
	( "user", SHA2("user",512), FALSE), /* Non-admin user */
	( "0", SHA2("0",512), FALSE) /* Non-admin user */
	
;

/* Organizations */
/* Two Test Organizations */
INSERT INTO nildb.Organizations
	(org_id, org_name)
	VALUES
	( "FAKE", "Not a Real Organization"), 
	( "TEST", "Test Organization for Testing")
;

/* Player Entries */
/* Two Players per Organization */
INSERT INTO nildb.Players 
	( player_name, birthdate, gender, org_id, active)
	VALUES
	( "test_player_one", '2000-01-01', 'M', 'TEST', TRUE),
	( "test_player_two", '2000-02-02', 'F', 'TEST', TRUE ),
	( "test_player_three", '2000-03-03', 'F', 'FAKE', TRUE),
	( "test_player_four", '2000-04-04', 'M', 'FAKE', FALSE)
;

/* Organization Association/Access */
INSERT INTO nildb.Org_User
	( org_id, user_id )
	VALUES 
	( "FAKE", "1" ), /* Associate 1 with FAKE */
	( "TEST", "0" )  /* Associate 0 with TEST */
;


/* Assessments */
INSERT INTO nildb.Assessments
	( player_id, assessment_name, value, date_and_time )
	VALUES 
	( 1, "game1", "A", "2016-04-15 00:00:00" ), 
	( 1, "game1", "B", "2016-04-20 10:00:00" ),
	( 1, "game1", "C", "2016-04-20 12:00:00" )
;


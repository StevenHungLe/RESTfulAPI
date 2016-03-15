/* This file is to be used to create test data for the 
	"mydb" database 
*/ 

/* User accounts */
INSERT INTO mydb.Users 
   ( user_id, password, is_admin )
	VALUES 
	( "AdminUser", MD5("AdminPassword"), TRUE ),  /* Insert admin user */
	( "User_one", MD5("1"), FALSE), /* Non-admin user */
	( "User_two", MD5("password"), FALSE) /* Non-admin user */
;

/* Organizations */
/* Two Test Organizations */
INSERT INTO mydb.Organizations
	(org_id, org_name)
	VALUES
	( "FAKE", "Not a Real Organization"), 
	( "TEST", "Test Organization for Testing")
;

/* Player Entries */
/* Two Players per Organization */
INSERT INTO mydb.Players 
	( player_name, birthdate, gender, org_id, active)
	VALUES
	( "test_player_one", '2000-01-01', 'M', 'TEST', TRUE),
	( "test_player_two", '2000-02-02', 'F', 'TEST', TRUE ),
	( "test_player_three", '2000-03-03', 'F', 'FAKE', TRUE),
	( "test_player_four", '2000-04-04', 'M', 'FAKE', FALSE)
;

/* Organization Association/Access */
INSERT INTO mydb.Org_User
	( org_id, usr_id )
	VALUES 
	( "FAKE", "User_one" ), /* Associate User_one with FAKE */
	( "TEST", "User_two" )  /* Associate User_two with TEST */
;


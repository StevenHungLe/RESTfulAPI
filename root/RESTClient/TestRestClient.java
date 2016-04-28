/*
 * Test Class for Class RestClient
 */
import static org.junit.Assert.*;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.junit.Test;

public class TestRestClient {
	
/************************************************
 * 												*
 * TEST CASES FOR LOGIN METHOD					*
 * 												*
 ************************************************/
	// CASE: successful login
	// expected outcome: 	-status code 200
	// 						-response body contains access_token that starts with the user name
	@Test
	public void successfulLogin() throws Exception {
		
		RestClient client = new RestClient("admin");
		ServerResponse resp = client.login("admin123456");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().startsWith("admin"));
		
		client.logout();
	}
	
	
	// CASE: failed login due to wrong user name
	// expected outcome: 	-status code 401
	// 						-response body contains error message
	@Test
	public void wrongUserNameLogin() throws Exception {

		RestClient client = new RestClient("someDistortedName");
		ServerResponse resp = client.login("admin123456");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals("AUTHENTICATION FAILED"));
	}
	
	
	// CASE: failed login due to wrong password
	// expected outcome: 	-status code 401
	// 						-response body contains error message
	@Test
	public void wrongPasswordLogin() throws Exception {
		
		RestClient client = new RestClient("admin");
		ServerResponse resp = client.login("someAbsurdPassword");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals("AUTHENTICATION FAILED"));
	}
	
	
	
	
/************************************************
 * 												*
 * TEST CASES FOR LOGOUT METHOD					*
 * 												*
 ************************************************/
	// CASE: successful logout
	// expected outcome: 	-status code 200
	@Test
	public void successfulLogout() throws Exception {
		
		RestClient client = new RestClient("admin");
		client.login("admin123456");
		
		ServerResponse resp = client.logout();
		assertTrue(resp.getStatusCode() == 200);
	}
	
	
	// CASE: failed logout due to failed login
	// expected outcome: 	-status code 401
	// 						-response body contains error message
	@Test
	public void failedLogout() throws Exception {

		RestClient client = new RestClient("SomeWeirdName");
		client.login("SomeWeirdPassword");
		
		ServerResponse resp = client.logout();
		assertFalse(resp.getStatusCode() == 200);
	}
	

/************************************************
 * 												*
 * TEST CASES FOR CREATE NEW USER METHOD		*
 * 												*
 ************************************************/
	// CASE: successful user creation by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulNewUserCreation() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.createNewUser("test1111", "test1111", 1);
		
		assertTrue(resp.getStatusCode() == 200);
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteUser("test1111");
		
		assertTrue(resp.getStatusCode() == 200);
		
		admin.logout();
	}
	
	
	// CASE: failed user creation because the action is attempted by a normal user
	// expected outcome: 	-status code 401
	@Test
	public void failedNewUserCreation() throws Exception {

		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		ServerResponse resp = normalUser.createNewUser("hungle1001", "Hung Le 1001", 1);
		
		assertTrue(resp.getStatusCode() == 401);
		
		resp = normalUser.deleteUser("hungle1001");
		
		assertTrue(resp.getStatusCode() == 401);
		
		normalUser.logout();
	}
	
	
/************************************************
 * 												*
 * TEST CASES FOR CREATE NEW ORGANIZATION 		*
 * 												*
 ************************************************/
	// CASE: successful organization creation by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulNewOrgCreation() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.createNewOrg("123456", "Org 1011");
		
		assertTrue(resp.getStatusCode() == 200);
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteOrg("123456");
		
		assertTrue(resp.getStatusCode() == 200);
		
		admin.logout();
	}
	
	
	// CASE: failed organization creation because the action is attempted by a normal user
	// expected outcome: 	-status code 401
	@Test
	public void failedNewOrgCreation() throws Exception {

		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		ServerResponse resp = normalUser.createNewOrg("123456", "Org 1011");
		
		assertTrue(resp.getStatusCode() == 401);
		
		resp = normalUser.deleteOrg("123456");
		
		assertTrue(resp.getStatusCode() == 401);
		
		normalUser.logout();
	}
	

/*************************************************
 * 												 *
 * TEST CASES FOR CREATE NEW ORG_USER ASSOCIATION*
 * 												 *
 *************************************************/
	// CASE: successful association creation by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulNewAssocCreation() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.assocOrgWithUser("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 200);
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteAssociation("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 200);
		
		admin.logout();
	}
	
	
	// CASE: failed association creation because the action is attempted by a normal user
	// expected outcome: 	-status code 401
	@Test
	public void failedNewAssocCreation() throws Exception {

		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		ServerResponse resp = normalUser.assocOrgWithUser("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 401);
		
		resp = normalUser.deleteAssociation("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 401);
		
		normalUser.logout();
	}
	
	
/*************************************************
 * 												 *
 * TEST CASES FOR CREATE NEW PLAYER   			 *
 * 												 *
 *************************************************/
	// CASE: successful player creation by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulNewPlayerCreation() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.createNewPlayer("hungle2222", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 200);
		
		String player_id = resp.getResponseBody();
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deletePlayer(player_id);
		
		assertTrue(resp.getStatusCode() == 200);
		
		admin.logout();
	}
	
	
	// CASE: failed player creation because the action is attempted by a normal user
	// expected outcome: 	-status code 401
	@Test
	public void failedNewPlayerCreation() throws Exception {

		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		ServerResponse resp = normalUser.createNewPlayer("hungle2", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 401);
		
		resp = normalUser.deletePlayer("just-a-test");
		
		assertTrue(resp.getStatusCode() == 401);
		
		normalUser.logout();
	}	
	
	// CASE: successful player creation by a user associated with the organization 
	// expected outcome: 	-status code 200
	@Test
	public void successfulNewPlayerCreationByNorMalUser() throws Exception {
		
		RestClient associatedUser = new RestClient("0");
		associatedUser.login("0");
		
		ServerResponse resp = associatedUser.createNewPlayer("hungle1234", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 200);
		
		String player_id = resp.getResponseBody();
		
		// it has to be the admin who deletes the player
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		resp = admin.deletePlayer(player_id);
		
		assertTrue(resp.getStatusCode() == 200);
		
		associatedUser.logout();
		admin.logout();
	}
	
	
/*************************************************
 * 												 *
 * TEST CASES FOR CREATE NEW ASSESSMENTS   		 *
 * 												 *
 *************************************************/
	// CASE: successful assessment creation by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulNewAssessmentCreation() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.addNewAssessment(1, "AAAAAAA", "A", "2016-04-12 10:00:00");
		
		assertTrue(resp.getStatusCode() == 200);
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteAssessment(1, "AAAAAAA", "A", "2016-04-12 10:00:00");
		
		assertTrue(resp.getStatusCode() == 200);
		
		admin.logout();
	}
	
	
	// CASE: failed assessment creation because the action is attempted by a normal user
	// expected outcome: 	-status code 401
	@Test
	public void failedNewAssessmentCreation() throws Exception {

		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		ServerResponse resp = normalUser.addNewAssessment(1, "justATest", "A", "2016-04-12 10:00:00");
		
		assertTrue(resp.getStatusCode() == 401);
		
		resp = normalUser.deleteAssessment(1, "justATest", "A", "2016-04-12 10:00:00");
		
		assertTrue(resp.getStatusCode() == 401);
		
		normalUser.logout();
	}	
	
	// CASE: successful assessment creation by a user associated with the organization 
	// expected outcome: 	-status code 200
	@Test
	public void successfulNewAssessmentCreationByNorMalUser() throws Exception {
		
		RestClient associatedUser = new RestClient("0");
		associatedUser.login("0");
		
		ServerResponse resp = associatedUser.addNewAssessment(1, "justATest", "A", "2016-04-12 10:00:00");
		assertTrue(resp.getStatusCode() == 200);
		
		// it has to be the admin who deletes the assessment
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		resp = admin.deleteAssessment(1, "justATest", "A", "2016-04-12 10:00:00");
		
		assertTrue(resp.getStatusCode() == 200);
		
		associatedUser.logout();
		admin.logout();
	}

	
	
	
/*************************************************
 * 												 *
 * TEST CASES FOR CHANGE PASSWORD METHOD  		 *
 * 												 *
 *************************************************/
	// CASE: successful password change by providing the correct current password
	// expected outcome: 	-status code 200
	@Test
	public void successfulPasswordChance() throws Exception {
		
		RestClient user = new RestClient("user");
		user.login("user");
		
		ServerResponse resp = user.changePassword("user", "user123456");
		
		assertTrue(resp.getStatusCode() == 200);
		
		// change the testing password back to the old password
		resp = user.changePassword("user123456", "user");
		
		assertTrue(resp.getStatusCode() == 200);
		
		user.logout();
	}
	
	
	// CASE: failed password change by providing wrong current password
	// expected outcome: 	-status code 401
	@Test
	public void failedPasswordChance() throws Exception {
		RestClient user = new RestClient("user");
		user.login("user");
		
		ServerResponse resp = user.changePassword("wrong_old_password", "newpassword");
		
		assertTrue(resp.getStatusCode() == 401);
		
		user.logout();
	}
		
	
	
/*************************************************
 * 												 *
 * TEST CASES FOR ADD LOG METHOD 	   			 *
 * 												 *
 *************************************************/
	// CASE: successful adding a log by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulLogAddition() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
		ServerResponse resp = admin.add_log("1", "whatever", date.format(new Date()),
				"started a new game"
				+ "\ncompleted level 1. Score: 100"
				+ "\ncompleted level 2. Score: 94" );
		
		assertTrue(resp.getStatusCode() == 200);
		
		admin.logout();
	}
	
	
	// CASE: failed adding log by a normal user not associated with the organization
	// expected outcome: 	-status code 401
	@Test
	public void failedLogAddition() throws Exception {

		RestClient normal_user = new RestClient("user");
		normal_user.login("user");
		
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
		ServerResponse resp = normal_user.add_log("1", "normal_user_fail_test", date.format(new Date()),
				"started a new game"
				+ "\ncompleted level 1. Score: 100"
				+ "\ncompleted level 2. Score: 94" );
		
		assertTrue(resp.getStatusCode() == 401);
		
		normal_user.logout();
	}
	
	// CASE: successful adding log by a user associated with the organization 
	// expected outcome: 	-status code 200
	@Test
	public void successfulLogAdditionByNormalUser() throws Exception {
		
		RestClient associatedUser = new RestClient("0");
		associatedUser.login("0");
		
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
		ServerResponse resp = associatedUser.add_log("1", "normal_user_fail_test", date.format(new Date()),
				"started a new game"
				+ "\ncompleted level 1. Score: 100"
				+ "\ncompleted level 2. Score: 94" );
		
		assertTrue(resp.getStatusCode() == 200);
		
		associatedUser.logout();
	}
	
	
	
/*************************************************
 * 												 *
 * TEST CASES FOR GET MOST RECENT ASSESSMENT	 *
 * 												 *
 *************************************************/
	// CASE: successful retrieval of the value by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulValueRetrieval() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.getMostRecentAssessment(1, "game1");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("C") );
		
		admin.logout();
	}
	
	
	// CASE: failed retrieval of the value by a normal user not associated with the organization
	// expected outcome: 	-status code 401
	@Test
	public void failedValueRetrieval() throws Exception {

		RestClient normalUser = new RestClient("0");
		normalUser.login("0");
		
		ServerResponse resp = normalUser.getMostRecentAssessment(3, "game1");
		
		assertTrue(resp.getStatusCode() == 401);
		
		normalUser.logout();
	}
	
	// CASE: successful retrieval of the value by a user associated with the organization 
	// expected outcome: 	-status code 200
	@Test
	public void successfulValueRetrievalByNormalUser() throws Exception {
		
		RestClient associatedUser = new RestClient("0");
		associatedUser.login("0");
		
		ServerResponse resp = associatedUser.getMostRecentAssessment(1, "game1");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("C") );
		
		associatedUser.logout();
	}
	
	
	
/*************************************************
 * 												 *
 * TEST CASES FOR GET ASSESSMENT BY DATE 		 *
 * 												 *
 *************************************************/
	// CASE: successful retrieval of the value by an admin
	// expected outcome: 	-status code 200
	@Test
	public void successfulValueRetrievalByDate() throws Exception {
		
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.getAssessmentByDate(1, "game1","2016-04-20");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("C") );
		
		
		resp = admin.getAssessmentByDate(1, "game1","2016-04-17");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("A") );
		
		admin.logout();
	}
	
	
	// CASE: failed retrieval of the value by a normal user
	// expected outcome: 	-status code 401
	@Test
	public void failedValueRetrievalByDate() throws Exception {

		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		ServerResponse resp = normalUser.getAssessmentByDate(1, "game1","2016-04-20");
		
		assertTrue(resp.getStatusCode() == 401);
		
		normalUser.logout();
	}

}

/*
 * Test Class for Class RestClient
 */
import static org.junit.Assert.*;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.junit.Test;

public class TestRestClient {

	// Test login method
	@Test
	public void testLogin() throws Exception {
		
		// CASE: successful log-in
		RestClient client = new RestClient("admin");
		ServerResponse resp = client.login("admin123456");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().startsWith("admin"));
		
		
		// CASE: log-in with a wrong user name
		client = new RestClient("someDistortedName");
		resp = client.login("admin123456");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals("AUTHENTICATION FAILED"));
		
		// CASE: log-in with a wrong password
		client = new RestClient("admin");
		resp = client.login("someAbsurdPassword");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals("AUTHENTICATION FAILED"));
	}
	
	
	// Test logout method
	@Test
	public void testLogout() throws Exception {
		
		// CASE: successful log-in & successful log-out
		RestClient client = new RestClient("admin");
		client.login("admin123456");
		
		ServerResponse resp = client.logout();
		assertTrue(resp.getStatusCode() == 200);
		
		
		// CASE: failed log-in -> failed log-out
		client = new RestClient("SomeWeirdName");
		client.login("SomeWeirdPassword");
		
		resp = client.logout();
		assertTrue(resp.getStatusCode() != 200);
	}
	
	
	// Test createNewUser method
	@Test
	public void testCreateNewUser() throws Exception {
		
		// CASE: admin successfully creates a new user
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.createNewUser("test1111", "test1111", 1);
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteUser("test1111");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		
		// CASE: normal user get declined for unauthorized action
		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		resp = normalUser.createNewUser("hungle1001", "Hung Le 1001", 1);
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
		resp = normalUser.deleteUser("hungle1001");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}
	
	
	
	// Test changePassword method
	@Test
	public void testChangePassword() throws Exception {
		
		// CASE: a user successfully change password by providing correct current password
		RestClient user = new RestClient("user");
		user.login("user");
		
		
		ServerResponse resp = user.changePassword("user", "user123456");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// change the testing password back to the old password
		resp = user.changePassword("user123456", "user");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		
		// CASE: user fails to change password by providing incorrect old password
		resp = user.changePassword("wrong_old_password", "newpassword");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}
	
	
	// Test createNewOrg method
	@Test
	public void testCreateNewOrg() throws Exception {
		
		// CASE: admin successfully creates a new organization
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.createNewOrg("123456", "Org 1011");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteOrg("123456");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		
		// CASE: normal user get declined for unauthorized action
		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		resp = normalUser.createNewOrg("123456", "Org 1011");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
		resp = normalUser.deleteOrg("123456");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}
	
	
	
	// Test assocOrgWithUser method
	@Test
	public void testAssocOrgWithUser() throws Exception {
		
		// CASE: admin successfully creates a new association
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.assocOrgWithUser("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteAssociation("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// CASE: normal user get declined for unauthorized action
		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		resp = normalUser.assocOrgWithUser("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
		resp = normalUser.deleteAssociation("TEST","admin");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}	
		
	
	// Test createNewPlayer method
	@Test
	public void testCreateNewPlayer() throws Exception {
		
		// CASE: admin successfully creates a new player
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.createNewPlayer("hungle2222", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 200);
		
		String player_id = resp.getResponseBody();
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deletePlayer(player_id);
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// CASE: normal user get declined for unauthorized action
		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		resp = normalUser.createNewPlayer("hungle2", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
		resp = normalUser.deletePlayer("just-a-test");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
		
		// CASE: normal user successfully create a player for their associated organization
		RestClient associatedUser = new RestClient("0");
		associatedUser.login("0");
		
		resp = associatedUser.createNewPlayer("hungle1234", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 200);
		
		player_id = resp.getResponseBody();
		
		
		// it has to be the admin who deletes the player
		resp = admin.deletePlayer(player_id);
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
	}
	
	
	// Test AddNewAssessment method
		@Test
		public void testAddNewAssessment() throws Exception {
			
			// CASE: admin successfully creates a new assessment
			RestClient admin = new RestClient("admin");
			admin.login("admin123456");
			
			ServerResponse resp = admin.addNewAssessment(1, "AAAAAAA", "A", "2016-04-12 10:00:00");
			
			assertTrue(resp.getStatusCode() == 200);
			
			assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
			
			// delete the testing entry, making sure the deletion is properly handled too
			resp = admin.deleteAssessment(1, "AAAAAAA", "A", "2016-04-12 10:00:00");
			
			assertTrue(resp.getStatusCode() == 200);
			assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
			
			// CASE: normal user get declined for unauthorized action
			RestClient normalUser = new RestClient("user");
			normalUser.login("user");
			
			resp = normalUser.addNewAssessment(1, "justATest", "A", "2016-04-12 10:00:00");
			
			assertTrue(resp.getStatusCode() == 401);
			assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
			
			resp = normalUser.deleteAssessment(1, "AAAAAAA", "A", "2016-04-12 10:00:00");
			
			assertTrue(resp.getStatusCode() == 401);
			assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
			
		}
		
	
	// Test add_log method
	@Test
	public void testAdd_Log() throws Exception {
		
		// CASE: admin successfully add a log
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
		ServerResponse resp = admin.add_log("1", "whatever", date.format(new Date()),
				"started a new game"
				+ "\ncompleted level 1. Score: 100"
				+ "\ncompleted level 2. Score: 94" );
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		
		// CASE: normal user successfully add a log for a player in his organization
		RestClient normal_user = new RestClient("0");
		normal_user.login("0");
		
		date = new SimpleDateFormat("yyyy/MM/dd");
		resp = normal_user.add_log("1", "normal_user_test", date.format(new Date()),
				"started a new game"
				+ "\ncompleted level 1. Score: 100"
				+ "\ncompleted level 2. Score: 94" );
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		
		// CASE: normal user fail to add log for a player not in his organization
		normal_user = new RestClient("user");
		normal_user.login("user");
		
		date = new SimpleDateFormat("yyyy/MM/dd");
		resp = normal_user.add_log("1", "normal_user_fail_test", date.format(new Date()),
				"started a new game"
				+ "\ncompleted level 1. Score: 100"
				+ "\ncompleted level 2. Score: 94" );
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}
	
	
	// Test GetMostRecentAssessment method
	@Test
	public void testGetMostRecentAssessment() throws Exception {
		
		// CASE: admin successfully carries out the action
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.getMostRecentAssessment(1, "game1");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("C") );
		
		
		// CASE: normalUser successfully carries out the action for the player he is associated with
		RestClient normalUser = new RestClient("0");
		normalUser.login("0");
		
		resp = normalUser.getMostRecentAssessment(1, "game1");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("C") );
		
		
		// CASE: normalUser cannot carry out the action for the player he is not associated with
		normalUser = new RestClient("0");
		normalUser.login("0");
		
		resp = normalUser.getMostRecentAssessment(3, "game1");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}
	
	
	// Test GetAssessmentByDate method
	@Test
	public void testGetAssessmentByDate() throws Exception {
		
		// CASE: admin successfully carries out the action
		RestClient admin = new RestClient("admin");
		admin.login("admin123456");
		
		ServerResponse resp = admin.getAssessmentByDate(1, "game1","2016-04-20");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("C") );
		
		
		resp = admin.getAssessmentByDate(1, "game1","2016-04-17");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals("A") );
		
		
		// CASE: normal user get declined for unauthorized action
		RestClient normalUser = new RestClient("user");
		normalUser.login("user"); // ATTENTION!!!
		
		resp = normalUser.getAssessmentByDate(1, "game1","2016-04-20");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}

}

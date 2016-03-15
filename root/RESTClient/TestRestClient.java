/*
 * Test Class for Class RestClient
 */
import static org.junit.Assert.*;

import org.junit.Test;

public class TestRestClient {

	// Test login method
	@Test
	public void testLogin() throws Exception {
		
		// CASE: successful log-in
		RestClient client = new RestClient("hungle");
		ServerResponse resp = client.login("hungle123456");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().startsWith("hungle"));
		
		
		// CASE: log-in with a wrong user name
		client = new RestClient("someDistortedName");
		resp = client.login("hungle123456");
		
		assertTrue(resp.getStatusCode() != 200);
		assertTrue(resp.getResponseBody().equals("WRONG USER_ID"));
		
		// CASE: log-in with a wrong password
		client = new RestClient("hungle");
		resp = client.login("someAbsurdPassword");
		
		assertTrue(resp.getStatusCode() != 200);
		assertTrue(resp.getResponseBody().equals("WRONG PASSWORD"));
	}
	
	
	// Test logout method
	@Test
	public void testLogout() throws Exception {
		
		// CASE: successful log-in & successful log-out
		RestClient client = new RestClient("hungle");
		client.login("hungle123456");
		
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
		RestClient admin = new RestClient("hungle");
		admin.login("hungle123456");
		
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
		
	}
	
	
	// Test createNewOrg method
	@Test
	public void testCreateNewOrg() throws Exception {
		
		// CASE: admin successfully creates a new organization
		RestClient admin = new RestClient("hungle");
		admin.login("hungle123456");
		
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
		
		resp = normalUser.createNewPlayer("hung le 2", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}
	
	
	
	// Test assocOrgWithUser method
	@Test
	public void testAssocOrgWithUser() throws Exception {
		
		// CASE: admin successfully creates a new association
		RestClient admin = new RestClient("hungle");
		admin.login("hungle123456");
		
		ServerResponse resp = admin.assocOrgWithUser("TEST","hungle");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deleteAssociation("TEST","hungle");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// CASE: normal user get declined for unauthorized action
		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		resp = normalUser.assocOrgWithUser("TEST","hungle");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}
		
		
	
	// Test createNewPlayer method
	@Test
	public void testCreateNewPlayer() throws Exception {
		
		// CASE: admin successfully creates a new player
		RestClient admin = new RestClient("hungle");
		admin.login("hungle123456");
		
		ServerResponse resp = admin.createNewPlayer("hungle2222", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// delete the testing entry, making sure the deletion is properly handled too
		resp = admin.deletePlayer("hungle2222");
		
		assertTrue(resp.getStatusCode() == 200);
		assertTrue(resp.getResponseBody().equals(RestClient.SUCCESS_MSG) );
		
		// CASE: normal user get declined for unauthorized action
		RestClient normalUser = new RestClient("user");
		normalUser.login("user");
		
		resp = normalUser.createNewPlayer("hung le 2", "1990-12-07", "M", 1, "TEST");
		
		assertTrue(resp.getStatusCode() == 401);
		assertTrue(resp.getResponseBody().equals(RestClient.UNAUTHORIZED_MSG) );
		
	}

}

/**
 * A REST Client created to test the RESTfulAPI
 * handles the encoding, decoding, sending and receipt of REST-protocol messages
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public class RestClient{
	
	// Instance variables \\
 	
	public static final String SUCCESS_MSG = "Operation Completed Successfully";
	
	public static final String UNAUTHORIZED_MSG = "User not authorized for this action";
	
 	private String userName;
 	
 	private String access_token;
 	
 	private HttpClient  client;
 	
 	private HttpGet httpRequest;
 	
 	private HttpResponse response;
 	
 	private BufferedReader reader;
 	
 	private JsonReader jsReader;
 	
 	private JsonObject jsObject;
	
	private PoolingHttpClientConnectionManager connManager ;
 	
 	// constructor
 	public RestClient(String userName) throws Exception
 	{
 		this.userName = userName;
 		this.access_token = "";
 		connManager = new PoolingHttpClientConnectionManager();
 		
 		httpRequest = null;
 		response = null;
 		reader = null;
    	jsReader = null;
 	}
 	
 	public void setUserName(String userName)
 	{
 		this.userName = userName;
 	}

 	
 	
 	/**
     * Encodes a login request.
     *
     * @param password the password used to log in
     * @return the decoded server response
     */
    public ServerResponse login(String password) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/login/%s/%s", this.userName, password);
    	httpRequest = new HttpGet(url);
    	response = client.execute(httpRequest);
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

    	jsReader = Json.createReader(reader);
    	jsObject = jsReader.readObject();
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	if (jsObject.toString().equals("{}"))
    		svrp.setStatusCode(500);
    	
    	svrp.setStatusCode(statusCode);
    	if( statusCode == 200 )
    	{
    		this.access_token = jsObject.getString("ACCESS_TOKEN");
    		svrp.setResponseBody(this.access_token);
    	}
    	else
    	{
    		String error_msg = jsObject.getString("error_msg");
    		svrp.setResponseBody(error_msg);
    		
    	}
    	
    	this.connManager.shutdown();
    	return svrp;
    }
    
    /**
     * Encodes a logout request.
     *
     * @return  the decoded server response
     */
    public ServerResponse logout() throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/login");
    	HttpDelete delete = new HttpDelete(url);
    	delete.setHeader("access_token", access_token);
    	response = client.execute(delete);
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
    	{
        	svrp.setResponseBody(SUCCESS_MSG);
    	}
    	else
    	{
    		jsReader = Json.createReader(reader);
        	JsonObject jsObject = jsReader.readObject();
    		String error_msg = jsObject.getString("error_msg");
        	svrp.setResponseBody(error_msg);
    	}
    	
    	this.connManager.shutdown();
    	return svrp;
    }
    
    
    /**
     * Encodes a create new user request.
     *
     * @return  the decoded server response
     */
    public ServerResponse createNewUser(String user_id, String password, int isAdmin) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/users");
    	HttpPost post = new HttpPost(url);
    	post.setHeader("access_token", access_token);

    	post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    	
    	String jsonString = Json.createObjectBuilder()
    			.add("USER_ID", user_id)
    			.add("PASSWORD", password)
    			.add("IS_ADMIN", isAdmin)
    			.build().toString();
    	
    	StringEntity params = new StringEntity(jsonString , "UTF-8"); 

    	params.setContentType("application/json; charset=UTF-8");

    	post.setEntity(params);
    	
    	response = client.execute(post);
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
    
    
    /**
     * Encodes a delete user request.
     *
     * @return  the decoded server response
     */
    public ServerResponse deleteUser(String user_id) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/users/%s",user_id);
    	HttpDelete delete = new HttpDelete(url);
    	delete.setHeader("access_token", access_token);
    	response = client.execute(delete);
    	
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
    
    
    /**
     * Encodes a create new organization request.
     *
     * @return   the decoded server response
     */
    public ServerResponse createNewOrg(String org_id, String org_name) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/orgs");
    	HttpPost post = new HttpPost(url);
    	post.setHeader("access_token", access_token);
    	post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    	
    	String jsonString = Json.createObjectBuilder()
    			.add("ORG_ID", org_id)
    			.add("ORG_NAME", org_name)
    			.build().toString();
    	
    	StringEntity params = new StringEntity(jsonString , "UTF-8"); 

    	params.setContentType("application/json; charset=UTF-8");

    	post.setEntity(params);
    	
    	
    	response = client.execute(post);
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
    
    
    /**
     * Encodes a delete org request.
     *
     * @return  the decoded server response
     */
    public ServerResponse deleteOrg(String org_id) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/orgs/%s",org_id);
    	HttpDelete delete = new HttpDelete(url);
    	delete.setHeader("access_token", access_token);
    	
    	response = client.execute(delete);
    	
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
    
    
    
    /**
     * Encodes a create new org_user association request.
     *
     * @return   the decoded server response
     */
    public ServerResponse assocOrgWithUser(String org_id, String usr_id) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/associations");
    	HttpPost post = new HttpPost(url);
    	post.setHeader("access_token", access_token);
    	post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    	
    	String jsonString = Json.createObjectBuilder()
    			.add("ORG_ID", org_id)
    			.add("USR_ID", usr_id)
    			.build()
    			.toString();
    	
    	StringEntity params = new StringEntity(jsonString , "UTF-8"); 

    	params.setContentType("application/json; charset=UTF-8");

    	post.setEntity(params);
    	
    	response = client.execute(post);
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
    
    
    /**
     * Encodes a delete association request.
     *
     * @return  the decoded server response
     */
    public ServerResponse deleteAssociation(String org_id, String usr_id) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/associations/%s/%s",org_id, usr_id);
    	HttpDelete delete = new HttpDelete(url);
    	delete.setHeader("access_token", access_token);
    	
    	response = client.execute(delete);
    	
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
    
    /**
     * Encodes a create new player request.
     *
     * @return   the decoded server response
     */
    public ServerResponse createNewPlayer(String player_name, String birthdate
    		, String gender, int active, String org_id) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/players");
    	HttpPost post = new HttpPost(url);
    	post.setHeader("access_token", access_token);
    	post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    	
    	String jsonString = Json.createObjectBuilder()
    			.add("player_name", player_name)
    			.add("birthdate", birthdate)
    			.add("gender", gender)
    			.add("active", active)
    			.add("org_id", org_id)
    			.build().toString();
    	
    	StringEntity params = new StringEntity(jsonString , "UTF-8"); 

    	params.setContentType("application/json; charset=UTF-8");

    	post.setEntity(params);
    	
    	response = client.execute(post);
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
    
    /**
     * Encodes a delete player request.
     *
     * @return  the decoded server response
     */
    public ServerResponse deletePlayer(String player_name) throws Exception
    {
    	ServerResponse svrp = new ServerResponse();
    	
    	this.connManager = new PoolingHttpClientConnectionManager();
    	client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    	String url = String.format("http://localhost/API/players/%s",player_name);
    	HttpDelete delete = new HttpDelete(url);
    	delete.setHeader("access_token", access_token);
    	
    	response = client.execute(delete);
    	
    	reader = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
    	
    	int statusCode = Integer.parseInt(response.getStatusLine().toString().split(" ")[1]);
    	
    	svrp.setStatusCode(statusCode);
    	
    	if( statusCode == 200)
        	svrp.setResponseBody(SUCCESS_MSG);
    	else if ( statusCode == 401 )
    		svrp.setResponseBody(UNAUTHORIZED_MSG);

    	this.connManager.shutdown();

    	return svrp;
    }
	
} // End class RestClient

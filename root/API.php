<?php	
/**
* 
* The main and single file of the RESTful API
* accepts and processes requests and maintains the underlying database
* 
*/

// establish connection with the database
$conn = new mysqli('localhost','hungle','hungle123456');
IF ( $conn->connect_error)
{
	die("can't connect:".$conn->connect_error);
}
			
	// obtain user full request
	$request = $_GET['request'];
	
	// using strtok with delimiter "/", split the request into tokens
	// The first token is the command indicating the action to be performed
	$command = strtok($request,"/");
	
	/**
	* 
	* Inspect the command to determine what operation is to be done
	* call the appropriate function passing the appropriate parameters
	* 
	*/
	
	// CASE: login command - the client is trying to log in or log out
	if ( strcmp($command,"login" ) == 0 )
	{
		// CASE: GET method - the client is trying to log in
		if ($_SERVER['REQUEST_METHOD'] == "GET" )
		{
			// get the needed parameters then call the login function
			$user_id = strtok("/");
			$password = strtok("/");
			login( $user_id , $password );
		}
		
		// CASE: DELETE method - the client is trying to log out
		elseif ($_SERVER['REQUEST_METHOD'] == "DELETE" )
		{
			// get the access_token from headers then call the logout function
			$access_token = getallheaders()['access_token'];
			logout( $access_token );
		}
		
	}
	
	
	// CASE: POST method - the client is trying to create a new entry 
	elseif( $_SERVER['REQUEST_METHOD'] == "POST" )
	{
		// Decode the Json-encoded request body into array of arguments
		$args = json_decode( file_get_contents('php://input'), true );
	
		// get the access_token from headers
		$access_token = getallheaders()['access_token'];
			
		// call function is_authorized to verify user's eligibility for this action
		if ( !is_authorized( $access_token, 1) )
		{
			http_response_code(401);
			die ("Unauthorized action");
		}
		
			
		// CASE: users command - the client is trying to create a new user
		elseif( $command == "users")
		{
			createNewUser($args['USER_ID'],$args['PASSWORD'],$args['IS_ADMIN']);
		}
		
		// CASE: orgs command - the client is trying to create a new organization
		elseif( $command == "orgs")
		{
			createNewOrg($args['ORG_ID'],$args['ORG_NAME']);
		}
		
		// CASE: associations command - the client is trying to create a new user-organization association
		elseif( $command == "associations")
		{
			assocOrgWithUser($args['ORG_ID'],$args['USR_ID']);
		}
		
		// CASE: players command - the client is trying to create a new player
		elseif( $command == "players")
		{
			createNewPlayer($args['player_name'],$args['birthdate'],$args['gender'],$args['active'],$args['org_id']);
		}
		
		// CASE: undefined operation
		else
		{
			http_response_code(500);
		}
	}
	
	
	// CASE: DELETE method - the client is trying to delete an entry 
	elseif( $_SERVER['REQUEST_METHOD'] == "DELETE" )
	{	
	
		// get the access_token from headers
		$access_token = getallheaders()['access_token'];
		
		// call function is_authorized to verify user's eligibility for this action
		if ( !is_authorized( $access_token, 1) )
		{
			http_response_code(401);
			die ("Unauthorized action");
		}
		
		// CASE: users command - the client is trying to delete a user
		if( $command == "users")
		{
			$user_id = strtok("/");
			deleteUser($user_id);
		}
		
		// CASE: orgs command - the client is trying to delete an organization
		elseif( $command == "orgs")
		{
			$org_id = strtok("/");
			deleteOrg($org_id);
		}
		
		// CASE: associations command - the client is trying to delete a user-organization association
		elseif( $command == "associations")
		{
			$org_id = strtok("/");
			$user_id = strtok("/");
			deleteAssociation($org_id,$user_id);
		}
		
		// CASE: players command - the client is trying to delete a player
		elseif( $command == "players")
		{
			$player_name = strtok("/");
			deletePlayer($player_name);
		}
		
		// CASE: undefined operation
		else
		{
			http_response_code(500);
		}
	}
	


// function: processes a log in request
function login($arg_user_id , $arg_password)
{
	global $conn;
	// retrieve the password of the requested username in the database
	$stmt = $conn->prepare('select password,is_admin from mydb.users where user_id=?');
	$stmt->bind_param("s",$arg_user_id);
	$stmt->bind_result($password,$is_admin);
	$stmt->execute();
	
	// check the password and send result of the authentication
	if ($stmt->fetch())
	{
		// password matched
		if ( $password == $arg_password )
		{
			// generate the access token for the user
			$access_token = uniqid($arg_user_id,true);
			
			$stmt->close();
			
			// create new entry in the database that maps access token to the user
			$stmt = $conn->prepare("INSERT INTO mydb.access_token(access_token,user_id,password,is_admin) VALUES (?,?,?,?)");
			$stmt->bind_param("ssss", $access_token, $arg_user_id, $password, $is_admin );
	
			// create mapping successful
			if ( $stmt->execute())
			{
				http_response_code(200); // OK status
				$js = array( "ACCESS_TOKEN" => $access_token );
				echo json_encode($js); // return the access token in the response
			}
		}
		
		// password not matched
		else
		{
			http_response_code(401);
			$js = array( "error_msg" => "WRONG PASSWORD" );
			echo json_encode($js);
		}
	}
	
	// user_id not found in database
	else
	{
		http_response_code(404);
		$js = array( "error_msg" => "WRONG USER_ID" );
		echo json_encode($js);
	}
	
}


// function: processes a log out request
function logout( $access_token )
{
	global $conn;
	
	// ensure that the access token is present in the database
	$stmt = $conn->prepare('select user_id from mydb.access_token where access_token=?');
	$stmt->bind_param("s",$access_token);
	$stmt->bind_result($query_result);
	$stmt->execute();
	
	// valid case: access_token exists in the database 
	if ($stmt->fetch())
	{

		$stmt->close();

		// delete the entry associated with the access token from the database
		$stmt = $conn->prepare("DELETE FROM mydb.access_token WHERE access_token=?");
		$stmt->bind_param("s",$access_token);
		
		// delete operation successful = log out successful
		if( $stmt->execute() )
		{
			http_response_code(200);
		}
		
		// delete failed
		else
		{
			http_response_code(500);
			$js = array( "error_msg" => "log out failed" );
			echo json_encode($js);
		}

	}
	
	// invalid case: access_token does not exist in the database
	else
	{
		http_response_code(404);
		$js = array( "error_msg" => "access_token unrecognized" );
		echo json_encode($js);
	}

}


// function: processes a request to create a new user
function createNewUser($user_id, $password, $is_admin)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO mydb.Users(user_id,password,is_admin) VALUES (?,?,?)");
	$stmt->bind_param("sss",$user_id,$password,$is_admin );
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to delete a user
function deleteUser($user_id)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM mydb.Users WHERE user_id = ?");
	$stmt->bind_param("s",$user_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to create new organization
function createNewOrg($org_id, $org_name)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO mydb.Organizations(org_id,org_name) VALUES (?,?)");
	$stmt->bind_param("ss",$org_id,$org_name);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
}

// function: processes a request to delete a organization
function deleteOrg($org_id)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM mydb.Organizations WHERE org_id = ?");
	$stmt->bind_param("s",$org_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
}



// function: processes a request to associate a user with an organization
function assocOrgWithUser($org_id, $usr_id)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO mydb.org_user(org_id,usr_id) VALUES (?,?)");
	$stmt->bind_param("ss",$org_id,$usr_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to delete a org_user association
function deleteAssociation($org_id, $usr_id)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM mydb.org_user WHERE org_id = ? AND usr_id = ?");
	$stmt->bind_param("ss",$org_id,$usr_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to create a new player
function createNewPlayer($player_name, $birthdate, $gender, $active, $org_id)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO mydb.players(player_name,birthdate,gender,active,org_id) VALUES (?,?,?,?,?)");
	$stmt->bind_param("sssss",$player_name,$birthdate,$gender,$active,$org_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to delete a player
function deletePlayer($player_name)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM mydb.players WHERE player_name=?");
	$stmt->bind_param("s",$player_name);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
}


//function: returns true if the given user has the required authorization, otherwise false
function is_authorized ($access_token,$is_admin) 
{
	global $conn;
	
	$sql = "SELECT is_admin FROM mydb.Access_token WHERE access_token='{$access_token}'";
	$result = $conn->query($sql);

	if($result AND $result->num_rows > 0)
	{
		$result = $result->fetch_assoc();
		if( $result['is_admin'] == $is_admin ) 
			return true;
	}
	return false;
}
	
?>
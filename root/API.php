<?php	


// establish connection with the database
$conn = new mysqli('localhost','hungle','hungle123456');
IF ( $conn->connect_error)
{
	die("can't connect:".$conn->connect_error);
}
	// obtain user full request
	$request = $_GET['request'];
	
	// obtain the command to determine what action the client is trying to do
	$command = strtok($request,"/");
	
	// CASE: login command - the client is trying to log in or log out
	if ( strcmp($command,"login" ) == 0 )
	{
		// CASE: GET method - the client is trying to log in
		if ($_SERVER['REQUEST_METHOD'] == "GET" )
		{
			$user_id = strtok("/");
			$password = strtok("/");
			login( $user_id , $password );
		}
		
		// CASE: DELETE method - the client is trying to log out
		elseif ($_SERVER['REQUEST_METHOD'] == "DELETE" )
		{
			$access_token = strtok("/");
			logout( $access_token );
		}
		
		
	}
	
	// CASE: POST method - the client is trying to create a new entry 
	elseif( $_SERVER['REQUEST_METHOD'] == "POST" )
	{
		// Decode the Json-encoded request body into array of arguments
		$args = json_decode( file_get_contents('php://input'), true );
	
	
		// CASE: users command - the client is trying to create a new user
		if( $command == "users")
		{
			if ( is_authorized( $args['ACCESS_TOKEN'], 1) )
			{
				createNewUser($args['USER_ID'],$args['PASSWORD'],$args['IS_ADMIN']);
			}
			else
				http_response_code(401);
		}
		
		// CASE: orgs command - the client is trying to create a new organization
		elseif( $command == "orgs")
		{
			if ( is_authorized( $args['ACCESS_TOKEN'], 1) )
			{
				createNewOrg($args['ORG_ID'],$args['ORG_NAME']);
			}
			else
				http_response_code(401);
		}
	}
	

// login function - faciliate a log in attempt
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


// logout function - faciliate a log out attempt
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


function createNewUser($user_id, $password, $is_admin)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO mydb.Users(user_id,password,is_admin) VALUES (?,?,?)");
	$stmt->bind_param("sss",$user_id,$password,$is_admin );
	
	//successful user creation
	if ( $stmt->execute())
	{
		http_response_code(200);
	}
	
	//failed
	else
	{
		http_response_code(500);
	}
		
}


function createNewOrg($org_id, $org_name)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO mydb.Organizations(org_id,org_name) VALUES (?,?)");
	$stmt->bind_param("ss",$org_id,$org_name);
	
	//successful organization creation
	if ( $stmt->execute())
	{
		http_response_code(200);
	}
	
	//failed
	else
		http_response_code(500);
		
}

/*
// POST METHOD
if ( $_SERVER['REQUEST_METHOD']=='POST')
{
	
	// Decode the Json request into array of arguments
	$args = json_decode( file_get_contents('php://input'), true );

	// ACTION: CREATE NEW USER
	if ( isset($args['USER_ID']) && isset($args['PASSWORD']) && isset($args['IS_ADMIN']))
	{
		// call function is_authorized to check whether this action is allowed...
		// for the user associated with the access token
		// if not, return 401 unauthorized and exit
		if ( !is_authorized($args['ACCESS_TOKEN'], 1))
		{
			http_response_code(401);
			exit(1);
		}
		
		$stmt = $conn->prepare("INSERT INTO mydb.Users(user_id,password,is_admin) VALUES (?,?,?)");
		$stmt->bind_param("sss",$args['USER_ID'],$args['PASSWORD'],$args['IS_ADMIN'] );
		
		//successful user creation
		if ( $stmt->execute())
		{
			http_response_code(200);
		}
		
		//failed
		else
			http_response_code(500);
	}
	
	
	// ACTION: CREATE NEW ORGANIZATION
	elseif ( isset($args['ORG_ID']) && isset($args['ORG_NAME']))
	{
		
		// call function is_authorized to check whether this action is allowed...
		// for the user associated with the access token
		// if not, return 401 unauthorized and exit
		if ( !is_authorized($args['ACCESS_TOKEN'], 1))
		{
			http_response_code(401);
			exit(1);
		}
		
		
		$stmt = $conn->prepare("INSERT INTO mydb.Organizations(org_id,org_name) VALUES (?,?)");
		$stmt->bind_param("ss",$args['ORG_ID'],$args['ORG_NAME']);
		
		//successful organization creation
		if ( $stmt->execute())
		{
			http_response_code(200);
		}
		
		//failed
		else
			http_response_code(500);
	}
	
	
	// ACTION: CREATE NEW ASSOCIATION BETWEEN A USER AND AN ORGANIZATION
	elseif ( isset($args['ORG_ID']) && isset($args['USR_ID']))
	{
		
		// call function is_authorized to check whether this action is allowed...
		// for the user associated with the access token
		// if not, return 401 unauthorized and exit
		if ( !is_authorized( $args['ACCESS_TOKEN'], 1 ))
		{
			http_response_code(401);
			exit(1);
		}
		
		
		$stmt = $conn->prepare("INSERT INTO mydb.org_user(org_id,usr_id) VALUES (?,?)");
		$stmt->bind_param("ss",$args['ORG_ID'],$args['USR_ID']);
		
		//successful association creation
		if ( $stmt->execute())
		{
			http_response_code(200);
		}
		
		//failed
		else
			http_response_code(500);
	}
	
	// ACTION: CREATE A NEW PLAYER IN AN ORGANIZATION
	elseif ( isset($args['player_name']) && isset($args['birthdate']) && isset($args['gender'])
	&& isset($args['active']) && isset($args['org_id']) )
	{
		
		// call function is_authorized to check whether this action is allowed...
		// for the user associated with the access token
		// if not, return 401 unauthorized and exit
		if ( !is_authorized( $args['ACCESS_TOKEN'], 1 ))
		{
			http_response_code(401);
			exit(1);
		}
		
		
		$stmt = $conn->prepare("INSERT INTO mydb.players(player_name,birthdate,gender,active,org_id) VALUES (?,?,?,?,?)");
		$stmt->bind_param("sssss",$args['player_name'],$args['birthdate'],$args['gender'],$args['active']
		,$args['org_id']);
		
		//successful association creation
		if ( $stmt->execute())
		{
			http_response_code(200);
		}
		
		//failed
		else
			http_response_code(500);
	}
	
	// Default: undefined operation
	else
	{
		http_response_code(400);
	}
}
*/

//function authenticate 
//Returns true if the given user has the required authorization
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
/**
 * Encodes a server response
 */


public class ServerResponse {
    private String responseBody;
    private int statusCode;

    /**
     * @param   from    sender
     * @param   to      list of recipients
     */
    public ServerResponse() {
    	this(0,null);
    }
    
    /**
     * @param   from    sender
     * @param   to      list of recipients
     */
    public ServerResponse(int statusCode, String responseBody) {
    	this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getResponseBody() {
		if(this.responseBody == null)
			return "Unexpected Error";
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}

package com.sunmi.pda.exceptions;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;

public class AuthorizationException extends Exception {
	
	private static final long serialVersionUID = 1L;
    private static final AndroidApplication app = AndroidApplication.getInstance();
	public AuthorizationException()
    {  super(app.getString(R.string.text_service_authorization_error));
    }
	
	public AuthorizationException(final String s)
    {  
		 super(s);
    }   
}

package com.android.pda.exceptions;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;

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

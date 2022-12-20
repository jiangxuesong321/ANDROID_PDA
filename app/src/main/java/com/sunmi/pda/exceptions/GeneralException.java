package com.sunmi.pda.exceptions;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;

public class GeneralException extends Exception {

	private static final long serialVersionUID = 1L;
    private static final SunmiApplication app = SunmiApplication.getInstance();
	public GeneralException() {
        super(app.getString(R.string.text_service_failed));

    }

	public GeneralException(final String s)
    {  
		 super(s);
    }   
}

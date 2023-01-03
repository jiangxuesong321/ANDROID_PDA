package com.sunmi.pda.exceptions;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;

public class GeneralException extends Exception {

	private static final long serialVersionUID = 1L;
    private static final AndroidApplication app = AndroidApplication.getInstance();
	public GeneralException() {
        super(app.getString(R.string.text_service_failed));

    }

	public GeneralException(final String s)
    {  
		 super(s);
    }   
}

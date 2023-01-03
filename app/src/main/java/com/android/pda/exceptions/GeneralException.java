package com.android.pda.exceptions;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;

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

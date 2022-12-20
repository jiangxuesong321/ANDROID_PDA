package com.sunmi.pda.broadcastreceivers;



import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.MasterTask;

import com.sunmi.pda.log.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmsBroadcastReceiver extends BroadcastReceiver {
	
	
	private static final String TAG = AlarmsBroadcastReceiver.class.getSimpleName();


	public AlarmsBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			int e_requestCode = bundle.getInt("RequestCode");
			if (e_requestCode == SunmiApplication.REQUESTCODE_MATERIAL_SYNC) {
				MasterTask task = new MasterTask(context, null);
				task.execute();
				LogUtils.d(TAG, "AlarmsBroadcast Receivered...");
			}
		} catch (Exception e) {
			LogUtils.i(TAG, e.getMessage());
		}
	}
}

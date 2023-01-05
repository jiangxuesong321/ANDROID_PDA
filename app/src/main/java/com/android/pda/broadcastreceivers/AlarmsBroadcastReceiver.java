package com.android.pda.broadcastreceivers;



import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.MasterTask;

import com.android.pda.log.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmsBroadcastReceiver extends BroadcastReceiver {
	
	
	private static final String TAG = AlarmsBroadcastReceiver.class.getSimpleName();


	public AlarmsBroadcastReceiver() {
	}

	/**
	 * 接收广播，更新主数据信息
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			int e_requestCode = bundle.getInt("RequestCode");
			if (e_requestCode == AndroidApplication.REQUESTCODE_MATERIAL_SYNC) {
				MasterTask task = new MasterTask(context, null);
				task.execute();
				LogUtils.d(TAG, "AlarmsBroadcast Received...");
			}
		} catch (Exception e) {
			LogUtils.i(TAG, e.getMessage());
		}
	}
}

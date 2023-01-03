package com.android.pda.utils;


import com.android.pda.models.TransferOrder;

import java.util.Comparator;

public class ComparatorTransferOrderItem implements Comparator<TransferOrder>{

	public int compare(TransferOrder lhs, TransferOrder rhs) {
		
		return lhs.getReservedItemNo().compareTo(rhs.getReservedItemNo());
	}
}

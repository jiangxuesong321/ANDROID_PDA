package com.sunmi.pda.utils;


import com.sunmi.pda.models.TransferOrder;

import java.util.Comparator;

public class ComparatorTransferOrderItem implements Comparator<TransferOrder>{

	public int compare(TransferOrder lhs, TransferOrder rhs) {
		
		return lhs.getReservedItemNo().compareTo(rhs.getReservedItemNo());
	}
}

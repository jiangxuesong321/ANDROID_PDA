package com.sunmi.pda.utils;


import com.sunmi.pda.models.Picking;
import com.sunmi.pda.models.PurchaseOrderSubContract;

import java.util.Comparator;

public class ComparatorPoSubContractItem implements Comparator<PurchaseOrderSubContract>{

	public int compare(PurchaseOrderSubContract lhs, PurchaseOrderSubContract rhs) {
		
		return lhs.getPurchaseOrderItem().compareTo(rhs.getPurchaseOrderItem());
	}
}

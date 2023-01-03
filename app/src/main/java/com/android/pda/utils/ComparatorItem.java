package com.android.pda.utils;


import com.android.pda.database.pojo.PurchaseOrder;

import java.util.Comparator;

public class ComparatorItem implements Comparator<PurchaseOrder>{

	public int compare(PurchaseOrder lhs, PurchaseOrder rhs) {
		
		return lhs.getPurchaseOrderItemNr().compareTo(rhs.getPurchaseOrderItemNr());
	}
}

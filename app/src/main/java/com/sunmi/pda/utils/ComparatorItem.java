package com.sunmi.pda.utils;


import com.sunmi.pda.database.pojo.PurchaseOrder;

import java.util.Comparator;

public class ComparatorItem implements Comparator<PurchaseOrder>{

	public int compare(PurchaseOrder lhs, PurchaseOrder rhs) {
		
		return lhs.getPurchaseOrderItemNr().compareTo(rhs.getPurchaseOrderItemNr());
	}
}

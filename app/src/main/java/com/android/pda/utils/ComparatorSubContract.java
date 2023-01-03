package com.android.pda.utils;

import com.android.pda.models.PurchaseOrderSubContract;

import java.util.Comparator;

public class ComparatorSubContract implements Comparator<PurchaseOrderSubContract>{

	public int compare(PurchaseOrderSubContract lhs, PurchaseOrderSubContract rhs) {
		
		return lhs.getPurchaseOrder().compareTo(rhs.getPurchaseOrder());
	}
}

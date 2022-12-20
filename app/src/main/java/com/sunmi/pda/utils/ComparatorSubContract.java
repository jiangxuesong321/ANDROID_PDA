package com.sunmi.pda.utils;

import com.sunmi.pda.models.PurchaseOrderSubContract;

import java.util.Comparator;

public class ComparatorSubContract implements Comparator<PurchaseOrderSubContract>{

	public int compare(PurchaseOrderSubContract lhs, PurchaseOrderSubContract rhs) {
		
		return lhs.getPurchaseOrder().compareTo(rhs.getPurchaseOrder());
	}
}

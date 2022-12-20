package com.sunmi.pda.utils;


import com.sunmi.pda.models.PrototypeBorrow;

import java.util.Comparator;

public class ComparatorPrototypeBorrowItem implements Comparator<PrototypeBorrow>{

	public int compare(PrototypeBorrow lhs, PrototypeBorrow rhs) {
		
		return lhs.getReservedItemNo().compareTo(rhs.getReservedItemNo());
	}
}

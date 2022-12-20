package com.sunmi.pda.utils;


import com.sunmi.pda.models.Picking;
import com.sunmi.pda.models.PrototypeBorrow;

import java.util.Comparator;

public class ComparatorPickingItem implements Comparator<Picking>{

	public int compare(Picking lhs, Picking rhs) {
		
		return lhs.getReservedItemNo().compareTo(rhs.getReservedItemNo());
	}
}

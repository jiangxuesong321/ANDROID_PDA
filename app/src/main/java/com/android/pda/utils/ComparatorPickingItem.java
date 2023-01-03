package com.android.pda.utils;


import com.android.pda.models.Picking;

import java.util.Comparator;

public class ComparatorPickingItem implements Comparator<Picking>{

	public int compare(Picking lhs, Picking rhs) {
		
		return lhs.getReservedItemNo().compareTo(rhs.getReservedItemNo());
	}
}

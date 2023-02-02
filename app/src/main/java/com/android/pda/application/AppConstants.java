package com.android.pda.application;

public class AppConstants {
    public static final int REQUEST_SUCCEED = 1;
    public static final int REQUEST_FAILED = 0;

    public static final int REQUEST_STAY = 10;
    public static final int REQUEST_BACK = 11;
    public static final int REQUEST_DELETE = 12;
    public static final int REQUEST_SPLIT = 16;

    public static final int REQUEST_OFFLINE_DATA = 13;
    public static final int REQUEST_LOGOUT = 14;
    public static final int OFFLINE_DATE_EXISTING = 15;
    public static final int REQUEST_CONFIRM_BACK = 17;

    public static final String ACTION_DATA_CODE_RECEIVED = "com.android.scanner.ACTION_DATA_CODE_RECEIVED";
    public static final String DATA = "data";
    public static final String SOURCE = "source_byte";

    //•	00 基础数据
    public static final String FUNCTION_ID_BASE_DATA = "00";
    //•	01 销售发货
    public static final String FUNCTION_ID_SALES_INVOICE = "01";
    //•	02 采购收货
    public static final String FUNCTION_ID_PURCHASE_ORDER = "02";
    //•	03 借出发货 --- 样机借用发货
    public static final String FUNCTION_ID_LEND = "03";
    //•	04 领料单发料 --- 材料领用
    public static final String FUNCTION_ID_PICKING_MATERIAL = "04";
    //•	05 借机领用 -- 样机领用
    public static final String FUNCTION_ID_PICKING = "05";
    //•	06 借机还回
    public static final String FUNCTION_ID_LEND_BACK = "06";
    //•	07 库存转移
    public static final String FUNCTION_ID_STOCK_MOVE = "07";
    //•	08 调拨发出
    public static final String FUNCTION_ID_TRANSFER_OUT = "08";
    //•	09 调拨接收
    public static final String FUNCTION_ID_TRANSFER_IN = "09";
    //•	10 序列号查询
    public static final String FUNCTION_ID_SN_SEARCH = "10";
    //•	11 发货指令
    public static final String FUNCTION_ID_SHIP_ORDER = "11";
    //•	12 出入库报表
    public static final String FUNCTION_ID_SN_REPORT = "12";
    //•	13 手工打印
    public static final String FUNCTION_ID_PRINT_MANUAL = "13";

    public static final String FUNCTION_ID_PRINT_SHIPPING_LABEL = "14";

    public static final String FUNCTION_ID_PRINT_RECEIVE_LABEL = "15";

    //    public static final String FUNCTION_ID_NO_VALUE_IN_BOUND = "16";
    public static final String FUNCTION_ID_D66_TEST = "16";

    public static final String FUNCTION_ID_NO_VALUE_OUT_BOUND = "17";

    public static final String FUNCTION_ID_PURCHASE_ORDER_RETURN = "18";

    public static final String FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT = "19"; //委外发货

    public static final String FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_IN = "20"; //委外收货

    public static final String FUNCTION_ID_PURCHASE_ORDER_GI = "21";

    public static final String FUNCTION_ID_PURCHASE_ORDER_PGR = "22";
    public static final String FUNCTION_ID_PURCHASE_ORDER_PGR_PARTS = "221";
    public static final String FUNCTION_ID_PURCHASE_ORDER_PGR_COMPLETE = "222";

    public static final String FUNCTION_IDS_OTHERS = "03,04,05,08";

    public static final String LAST_SCAN = "LastScan";
}

package com.android.pda.controllers;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.models.POStorageQuery;
import org.apache.commons.lang3.StringUtils;

public class POStorageController {
    protected static final String TAG = POStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();

    /**
     * 验证查询参数：物料凭证号
     * @param query
     * @return
     */
    public String verifyQuery(POStorageQuery query) {
        if (query != null) {
            if (StringUtils.isEmpty(query.getMaterialDocument())) {
                return app.getString(R.string.text_input_material_doc_num);
            }
            // TODO: verify the digit
        }
        return null;
    }
}

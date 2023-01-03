package com.android.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.NoValueItem;
import com.android.pda.models.NoValueRequest;
import com.android.pda.models.SerialInfo;
import com.android.pda.utils.DateUtils;
import com.android.pda.utils.HttpRequestUtil;
import com.android.pda.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NoValueController {
    protected static final String TAG = NoValueController.class.getSimpleName();
    private static final AndroidApplication app = AndroidApplication.getInstance();

    public NoValueRequest generateRequest(
            List<SerialInfo> serialInfos,
            String storageLocation,
            String receivingLocation,
            String plant,
            String confirmDate,
            String documentType,
            String movementType
    ) {
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        List<NoValueItem> items = generateItem(serialInfos, storageLocation, receivingLocation, plant, movementType);
        NoValueRequest request = new NoValueRequest("01", confirmDate + "T00:00:00", documentDate, "", documentType, items);
        return request;
    }

    private List<NoValueItem> generateItem(
            List<SerialInfo> serialInfos,
            String storageLocation,
            String receivingLocation,
            String plant,
            String movementType
    ) {
        List<NoValueItem> items = new ArrayList<>();
        Map<String, List<SerialInfo>> serialGroup =  new HashMap<>();
        serialInfos.stream()
                .collect(Collectors.groupingBy(SerialInfo::getGroupFiled,Collectors.toList()))
                .forEach(serialGroup::put);

        serialGroup.forEach((key, sInfos) -> {
            if (sInfos.size() < 1) {
                return;
            }
            Integer _total = 0;
            List<String> _sNumbers = new ArrayList<>();
            SerialInfo _info = sInfos.get(0);
            for (SerialInfo _sInfo: sInfos) {
                int _count = Integer.valueOf(_sInfo.getCount());
                _count = _count > 0 ? _count : 1;
                _total += _count;
                _sNumbers.add(_sInfo.getSerialnumber());
            }
            NoValueItem _item = new NoValueItem(_info.getMaterial(), _info.getBatch(), plant, storageLocation, receivingLocation, "", movementType, _total.toString(), "1", _sNumbers);
            items.add(_item);
        });
        return items;
    }

    public HttpResponse post(NoValueRequest request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_posting) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        Map<String, String> headers = http.getToken(url);
        headers.put(app.getString(R.string.header_language_key), app.getString(R.string.header_language_value));
        String body = JSON.toJSONString(request);
        HttpResponse response = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, body, headers);
        System.out.println(body);
        System.out.println(JSON.toJSONString(response));
        System.out.println(JSON.toJSONString(headers));
        if (response.getCode() != 201) {
            String errorMessage = Util.parseSapError(response.getResponseString());
            response.setError(errorMessage);
        } else {
            JSONObject jsonObject = JSONObject.parseObject(response.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            String MaterialDocument = d.getString("MaterialDocument");
            response.setResponseString(MaterialDocument);
        }
        return response;
    }
}

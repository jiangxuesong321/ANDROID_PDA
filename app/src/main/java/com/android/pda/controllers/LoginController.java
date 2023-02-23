package com.android.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.Login;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.utils.Algorithm;
import com.android.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginController {
    protected static final String TAG = LoginController.class.getSimpleName();
    public static final int FLAG_LOGIN = 1;
    public static final int FLAG_LOGOUT = 0;

    public static boolean isLogin;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private Login login;

    /**
     * 用户登录校验
     *
     * @param userId 用户ID
     * @param pwd    密码
     * @return
     * @throws Exception
     */
    public String login(String userId, String pwd) throws Exception {
        isLogin = true;
        String encryptPwd = Algorithm.encrypt(pwd);
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_login) + app.getString(R.string.sap_url_client);
        String postJson = "{\n" +
                "  \"ZUID\": \"" + userId + "\", \n" +
                "  \"ZUPWD\": \"" + pwd + "\"\n" +
                "}";
        LogUtils.e(TAG, "Login url--------->" + url);
        LogUtils.e(TAG, "Login postJson--------->" + postJson);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, postJson, null);

        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        try {
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            String result = jsonObject.getString("msgtyp");
            String msgtxt = jsonObject.getString("msgtxt");
            result = "S";
            if (StringUtils.equalsIgnoreCase(result, "S")) {
                return "";
            } else if (StringUtils.equalsIgnoreCase(result, "E")) {
                if (msgtxt != null) {
                    return msgtxt + ", Post Data: " + postJson;
                } else {
                    return app.getString(R.string.login_failed) + ", Post Data: " + postJson;
                }
            } else {
                return app.getString(R.string.text_service_failed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "Login Error ------> " + e.getMessage());
            return app.getString(R.string.text_service_failed);
        }
    }

//    /**
//     * 根据用户 ID 获取用户权限信息
//     *
//     * @param userId 用户ID
//     * @return
//     * @throws Exception
//     */
//    public String getUserPermission(String userId) throws Exception {
//        if (userId == null) {
//            return null;
//        }
//
//        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_permission) + app.getString(R.string.sap_url_client) + "&$filter=zuid eq '" + userId + "'";
//        String zuname = "";
//        LogUtils.e(TAG, "Login url--------->" + url);
//
//        HttpRequestUtil http = new HttpRequestUtil();
//        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
//
//        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
//        LogUtils.d(TAG, "Code--->" + httpResponse.getCode());
//        if (httpResponse.getCode() != 200) {
//            return Util.parseSapError(httpResponse.getResponseString());
//        }
//        try {
//            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
//            JSONObject d = jsonObject.getJSONObject("d");
//            JSONArray jsonArray = d.getJSONArray("results");
//            List<Login> all = new ArrayList<>();
//
//            for (int i = 0; i < jsonArray.size(); i++) {
//                try {
//                    JSONObject objectI = jsonArray.getJSONObject(i);
//                    String zuid = objectI.getString("zuid");
//                    String zfunc = objectI.getString("zfunc");
//                    String zfactory = objectI.getString("zfactory");
//                    String zstore_loc = objectI.getString("zstore_loc");
//                    zuname = objectI.getString("zuname");
//
//                    Login login = new Login(zuid, zfunc, zfactory, zstore_loc, zuname, "");
//
//                    all.add(login);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LogUtils.e(TAG, "get userPermission Error ------> " + e.getMessage());
//                }
//            }
//            String funcs = findUniqueFunc(all);
//            String facs = findUniqueFactory(all);
//            String locs = findUniqueLoc(all);
//            System.out.println("Zujson---" + all.get(0).getZujson());
//            Login user = new Login(userId, funcs, facs, locs, zuname, all.get(0).getZujson());
//            user.setZujson(JSON.toJSONString(all));
//            List<Login> logins = new ArrayList<>();
//            logins.add(user);
//            app.getDBService().getDatabaseServiceLogin().createData(logins);
//
//            return "";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtils.e(TAG, "Login Error ------> " + e.getMessage());
//            return app.getString(R.string.text_service_failed);
//        }
//    }

    /**
     * 根据用户 ID 获取用户权限信息,权限默认给全部99
     *
     * @param userId 用户ID
     * @return
     * @throws Exception
     */
    public String getUserPermission(String userId) throws Exception {
        List<Login> all = new ArrayList<>();
        if (userId == null) {
            return null;
        }
        try {
            Login user = new Login(userId, "99", "", "", "", "");
            user.setZujson(JSON.toJSONString(all));
            List<Login> logins = new ArrayList<>();
            logins.add(user);
            app.getDBService().getDatabaseServiceLogin().createData(logins);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "Login Error ------> " + e.getMessage());
            return app.getString(R.string.text_service_failed);
        }
    }

    public void logout(String token) {
        deleteLoginUser();
    }

    public Login getLoginUser() {
        if (login == null) {
            List<Login> users = app.getDBService().getDatabaseServiceLogin().getAllData();
            if (users != null && users.size() > 0) {
                login = new Login(users.get(0).getZuid(), users.get(0).getZfunc(), users.get(0).getZfactory(), users.get(0).getZstore_loc(), users.get(0).getZuname(), users.get(0).getZujson());
            }
        }
        return login;
    }

    public void deleteLoginUser() {
        login = null;
        app.getDBService().getDatabaseServiceLogin().deleteData();
    }

    /**
     * 用户 FUNC 分号分隔处理
     *
     * @param logins
     * @return
     */
    private String findUniqueFunc(List<Login> logins) {
        Set<String> set = new HashSet<String>();
        for (Login login : logins) {
            set.add(login.getZfunc());
        }
        return setToString((HashSet) set);
    }

    /**
     * 用户 Factory 分号分隔处理
     *
     * @param logins
     * @return
     */
    private String findUniqueFactory(List<Login> logins) {
        Set<String> set = new HashSet<String>();
        for (Login login : logins) {
            set.add(login.getZfactory());
        }
        return setToString((HashSet) set);
    }

    /**
     * 用户 Store Location 分号分隔处理
     *
     * @param logins
     * @return
     */
    private String findUniqueLoc(List<Login> logins) {
        Set<String> set = new HashSet<String>();
        for (Login login : logins) {
            set.add(login.getZstore_loc());
        }
        return setToString((HashSet) set);
    }

    /**
     * Set 分隔处理转 String
     *
     * @param set
     * @return
     */
    private String setToString(HashSet set) {
        String[] hashSetToArray = new String[set.size()];
        set.toArray(hashSetToArray);
        String str = StringUtils.join(hashSetToArray, ";");

        return str;
    }
}

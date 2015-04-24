package org.idaxiang.elephant.support;

/**
 * Created by Azzssss on 15-1-1.
 */
public class Constants {

    // 默认模板路径
    public static final String TEMPLATE_DEF_URL = "template.html";

    public static final String LEANCLOUD_APP_ID = "cvy3c3z01696t50wa0sodgftiskt64v1qltdu2fu54853e6w";

    public static final String LEANCLOUD_APP_KEY = "sdzhptu8r804vpy5dq3pihzxh136jank9v6c6bzomu6orovo";

    public static final String SINA_APP_KEY = "628627262";

    public static final String SINA_APP_SECRET = "6dc559c168a524c426b9a93a288d258c";

    public static final String ELEPHANT_MAGAZINE_BASE_URL = "http://backend.idaxiang.org/api/";

    // all articles
    public static final String ALL_ARTICLES = ELEPHANT_MAGAZINE_BASE_URL + "views/articles_view?args[0]=all&args[1]=all&created=&created_1=&limit=0";

    //base url
    private static final String URL_SINA_WEIBO = "https://api.weibo.com/2/";

    //login
    public static final String UID = URL_SINA_WEIBO + "account/get_uid.json";
    public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";

    public static final String SINA_BASE_URL = "https://api.weibo.com/2/";

    // Login
    public static final String OAUTH2_ACCESS_TOKEN = "https://api.weibo.com/oauth2/access_token";

    // User / Account
    public static final String GET_UID = SINA_BASE_URL + "account/get_uid.json";
    public static final String USER_SHOW = SINA_BASE_URL + "users/show.json";

    public static final String DIRECT_URL = "https://api.weibo.com/oauth2/default.html";

}

package org.idaxiang.elephant.support.utils;

import org.idaxiang.elephant.model.AccountBean;
import org.idaxiang.elephant.model.UserModel;

public class ObjectToStringUtility {



    public static String toString(UserModel bean) {
        return "user id=" + bean.getId()
                + "," + "name=" + bean.getScreen_name();
    }

    public static String toString(AccountBean account) {
        return account.getUsernick();
    }
}

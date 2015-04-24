package org.idaxiang.elephant.support.http;

import java.util.HashMap;

/**
 * Created by Azzssss on 15-1-2.
 */
public abstract class BaseParameters extends HashMap<String,Object> {

    public abstract String encode();

    public abstract Object[] toBoundaryMsg();
}

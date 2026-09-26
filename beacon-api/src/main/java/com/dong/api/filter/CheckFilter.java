package com.dong.api.filter;

import com.dong.common.model.StandardSubmit;

public interface CheckFilter{
    /**
     * 校验！！！！
     * @param submit
     */
    void check(StandardSubmit submit);
}

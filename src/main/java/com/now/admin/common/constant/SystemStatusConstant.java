package com.now.admin.common.constant;

import com.now.admin.common.exception.BadStatusException;

public interface SystemStatusConstant {

    /** 正常，允许 等正向状态 */
    Integer NORMAL_STATUS = 1;

    /** 禁止状态 */
    Integer FORBIDDEN_STATUS = 0;

    /** 异常状态 */
    Integer ABNORMAL = -1;


    static boolean isNormal(int state){
        return NORMAL_STATUS.equals(state);
    }

    /**
     * 验证状态
     * 1. NORMAL_STATUS ：继续
     * 2. FORBIDDEN_STATUS：throw new BadStatusException
     * 2. ABNORMAL：throw new BadStatusException
     * @param state 状态
     */
    static void validStatus(int state){
        if(NORMAL_STATUS.equals(state)){
            return;
        }
        if(FORBIDDEN_STATUS.equals(state)){
            throw new BadStatusException(AppStatusEnum.OTHER_ERROR.getCode(), "禁止状态，不允许操作");
        }
        if(ABNORMAL.equals(state)){
            throw new BadStatusException(AppStatusEnum.OTHER_ERROR.getCode(), "异常状态，不允许操作");
        }


    }


}

package com.now.admin.common.domain.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRsp{

    private Long id;

    private String userId;

    private String refreshToken;

    private String token;

}

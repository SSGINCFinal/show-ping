package com.ssginc.showping.dto.object;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private Long memberNo;
    private String memberId;
    private String memberName;
    private String memberEmail;
    private String memberPassword;
    private String memberAddress;
    private String memberPhone;
}

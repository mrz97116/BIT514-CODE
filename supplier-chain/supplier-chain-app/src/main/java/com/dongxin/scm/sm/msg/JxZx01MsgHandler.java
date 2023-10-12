package com.dongxin.scm.sm.msg;

import com.dongxin.msg.po.Msg;
import com.dongxin.msg.po.annotation.MsgHandler;
import com.dongxin.msg.po.handler.IMsgHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 经销发到直销系统的电文...随便写的demo
 *
 * @author Succy
 * create on 2020/10/31
 */
@MsgHandler(intfId = "JXZX01")
@Slf4j
public class JxZx01MsgHandler implements IMsgHandler {
    @Override
    public Object handle(Msg msg) throws Exception {
        log.info("接收到经销系统发送过来的电文:{}", msg.toJson());
        return null;
    }
}

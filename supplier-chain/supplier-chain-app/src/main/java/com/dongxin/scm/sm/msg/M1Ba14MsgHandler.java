package com.dongxin.scm.sm.msg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.msg.po.Msg;
import com.dongxin.msg.po.annotation.MsgHandler;
import com.dongxin.msg.po.handler.IMsgHandler;
import com.dongxin.scm.cm.entity.UserContrast;
import com.dongxin.scm.cm.service.UserContrastService;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.service.MatService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Succy
 */
@MsgHandler(intfId = "M1BA14")
@Slf4j
public class M1Ba14MsgHandler implements IMsgHandler {

    @Autowired
    UserContrastService userContrastService;

    @Autowired
    MatService matService;


    @Override
    public Object handle(Msg msg) throws Exception {
        log.info("接收到柳州基地发送过来的电文:{}", msg.toJson());
        List<Map<String, Object>> list = msg.getBody();

        for (Map<String, Object> map : list) {
            Object items = map.get("ITEM");
            String consignUserName = MapUtil.getStr(map, "CONSIGN_USER_NAME");
            UserContrast userContrast = new UserContrast();
            userContrast.setName(consignUserName);
            QueryWrapper<UserContrast> queryWrapper = QueryGenerator.initQueryWrapper(userContrast, null);
            queryWrapper.lambda().eq(UserContrast::getName, consignUserName);
            UserContrast userContrast1 = userContrastService.getOne(queryWrapper);
            if (ObjectUtil.isNull(userContrast1)) {
                return null;
            }

            List<Map> maps = Convert.toList(Map.class, items);

            List<Mat> matList = maps.stream()
                    .map(e -> {
                        Mat mat = BeanUtil.mapToBeanIgnoreCase(e, Mat.class, true);
                        String psc = MapUtil.getStr(e, "PSC");
                        mat.setProdClassCode(StrUtil.sub(psc, 0, 1));
                        //棒线不处理材料号
                        if ("B".equals(mat.getProdClassCode())
                                || "D".equals(mat.getProdClassCode())) {
                            mat.setMatNo("");
                        }
                        mat.setAvailableWeight(mat.getMatNetWt());
                        mat.setAvailableQty(mat.getMatNum());
                        mat.setTenantId(Integer.parseInt(userContrast1.getTenantCode()));
                        return mat;
                    })
                    .collect(Collectors.toList());
            System.out.println(matList);
            matService.saveBatch(matList);

        }
        return null;
    }

}

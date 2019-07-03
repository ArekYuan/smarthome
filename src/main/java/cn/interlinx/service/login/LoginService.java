package cn.interlinx.service.login;


import cn.interlinx.entity.Userinfo;
import org.springframework.transaction.annotation.Transactional;


/**
 * 登录业务逻辑接口类
 */
@Transactional
public interface LoginService {

    int insert(Userinfo record);

    Userinfo selectById(String openId);

    Userinfo selectByLogin(String userName, String passWord);

    Userinfo selectByUserId(Integer userId);

}

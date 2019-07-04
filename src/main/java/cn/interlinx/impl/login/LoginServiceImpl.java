package cn.interlinx.impl.login;

import cn.interlinx.dao.UserinfoMapper;
import cn.interlinx.entity.Userinfo;
import cn.interlinx.service.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 城市业务逻辑实现类
 * <p>
 * Created by bysocket on 07/02/2017.
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserinfoMapper mapper;


    @Override
    public int insert(Userinfo record) {
        return mapper.insert(record);
    }

    @Override
    public Userinfo selectByOpenId(String openId) {
        return mapper.selectByOpenId(openId);
    }

    @Override
    public Userinfo selectByLogin(String userName, String passWord) {
        return mapper.selectByLogin(userName, passWord);
    }

    @Override
    public Userinfo selectByUserId(Integer userId) {
        return mapper.selectByUserId(userId);
    }
}

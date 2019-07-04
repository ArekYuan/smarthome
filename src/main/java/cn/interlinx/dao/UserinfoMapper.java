package cn.interlinx.dao;

import cn.interlinx.entity.Userinfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserinfoMapper {

    int deleteByPrimaryKey(Integer userid);

    int insert(Userinfo record);

    Userinfo selectByOpenId(String openId);

    Userinfo selectByLogin(@Param("username") String username, @Param("password") String password);

    List<Userinfo> selectAll();

    int updateByPrimaryKey(Userinfo record);

    Userinfo selectByUserId(Integer userid);
}
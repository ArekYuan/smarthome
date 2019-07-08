package cn.interlinx.dao;

import cn.interlinx.entity.Feedback;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackMapper {
    int deleteByPrimaryKey(Integer feedbackId);

    int insert(Feedback record);

    Feedback selectByPrimaryKey(Integer feedbackId);

    List<Feedback> selectAll();

    int updateByPrimaryKey(Feedback record);
}
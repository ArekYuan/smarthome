package cn.interlinx.entity;

import java.io.Serializable;
import java.util.Date;

public class Feedback implements Serializable {
    private Integer feedbackId;

    private Integer userid;

    private String repairImgUrl;

    private String feedback;

    private Date repairTime;

    private String repairPhone;

    private String repairName;

    private Integer descType;

    public Integer getDescType() {
        return descType;
    }

    public void setDescType(Integer descType) {
        this.descType = descType;
    }

    public String getRepairName() {
        return repairName;
    }

    public void setRepairName(String repairName) {
        this.repairName = repairName;
    }

    private static final long serialVersionUID = 1L;

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getRepairImgUrl() {
        return repairImgUrl;
    }

    public void setRepairImgUrl(String repairImgUrl) {
        this.repairImgUrl = repairImgUrl;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Date getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(Date repairTime) {
        this.repairTime = repairTime;
    }

    public String getRepairPhone() {
        return repairPhone;
    }

    public void setRepairPhone(String repairPhone) {
        this.repairPhone = repairPhone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", feedbackId=").append(feedbackId);
        sb.append(", userid=").append(userid);
        sb.append(", repairImgUrl=").append(repairImgUrl);
        sb.append(", feedback=").append(feedback);
        sb.append(", repairTime=").append(repairTime);
        sb.append(", repairPhone=").append(repairPhone);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
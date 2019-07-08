package cn.interlinx.entity;

import java.io.Serializable;

public class KD_Info implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String kd_name;
    private String kd_num;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKd_name() {
        return kd_name;
    }

    public void setKd_name(String kd_name) {
        this.kd_name = kd_name;
    }

    public String getKd_num() {
        return kd_num;
    }

    public void setKd_num(String kd_num) {
        this.kd_num = kd_num;
    }



    @Override
    public String toString() {
        return "KD_Info{" +
                "id=" + id +
                ", kd_name='" + kd_name + '\'' +
                ", kd_num='" + kd_num + '\'' +
                '}';
    }
}

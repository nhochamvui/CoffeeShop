package com.example.coffeeshop;

public class SewerAuthorize {
    private Long SewerId;
    private String SewerAdd;
    private String SewerModify;
    private String SewerRemove;
    public SewerAuthorize() {

    }
    public SewerAuthorize(Long SewerId, String SewerAdd, String SewerModify, String SewerRemove){
        this.SewerAdd = SewerAdd;
        this.SewerRemove = SewerRemove;
        this.SewerModify = SewerModify;
        this.SewerId = SewerId;
    }

    public Long getSewerId() {
        return this.SewerId;
    }

    public void setSewerId(Long SewerId) {
        this.SewerId = SewerId;
    }

    public String getSewerAdd() {
        return this.SewerAdd;
    }

    public void setSewerAdd(String SewerAdd) {
        this.SewerAdd = SewerAdd;
    }

    public String getSewerModify() {
        return this.SewerModify;
    }

    public void setSewerModify(String SewerModify) {
        this.SewerModify = SewerModify;
    }

    public String getSewerRemove() {
        return this.SewerModify;
    }

    public void setSewerRemove(String SewerRemove) {
        this.SewerModify = SewerRemove;
    }


}

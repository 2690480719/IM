package com.lqb.android.im.model.bean;

// 用户bean类
public class UserInfo {
    private String name;    // 用户名称
    private String hxId;    // 环信ID，在环信服务器上的唯一标识
    private String nick;    // 用户的昵称
    private String photo;   // 头像

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHxId() {
        return hxId;
    }

    public void setHxId(String hxId) {
        this.hxId = hxId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public UserInfo() {

    }

    public UserInfo(String name) {
        this.name = name;
        this.hxId = name;
        this.nick = name;
    }

    @Override
    public String toString() {
        return "UserInfo(" +
                "name='" + name + '\'' +
                ", hxId='" + hxId + '\'' +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}

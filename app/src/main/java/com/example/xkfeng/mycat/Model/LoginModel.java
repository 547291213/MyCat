package com.example.xkfeng.mycat.Model;

/**
 * Created by initializing on 2018/10/7.
 */

public class LoginModel {

    private String id ;
    private String password ;
    private String isTopLogin ;
    private String lastUpdateTime ;

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getIsTopLogin() {
        return isTopLogin;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsTopLogin(String isTopLogin) {
        this.isTopLogin = isTopLogin;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}

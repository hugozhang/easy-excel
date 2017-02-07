package com.github.xutils.poi;

import java.util.Date;

public class User {

    @ExcelColumn(name = "年龄", width = 30)
    private int age;
    @ExcelColumn(name = "姓名", width = 30)
    private String username;
    @ExcelColumn(name = "公司", width = 30)
    private String company;
    @ExcelColumn(name = "地址", width = 30)
    private String address;
    @ExcelColumn(name = "生日", width = 30)
    private Date birthday;
    public User(){}
    
    public User(int age,String username){
        this.age = age;
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
    
}

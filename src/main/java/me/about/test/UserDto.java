package me.about.test;

public class UserDto {

    private String name;

    private Integer age;
    
    private UserInner inner;

    public UserInner getInner() {
        return inner;
    }

    public void setInner(UserInner inner) {
        this.inner = inner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

package me.about.test;

import com.baidu.unbiz.easymapper.MapperFactory;

import me.about.utils.BeanCopier;

public class BeanCopy {

    public static void main(String[] args) {
       
        User u = new User();
        u.setName("1");
        u.setAge(1);
        
        UserInner in = new UserInner();
        in.setTest("hello");
        u.setInner(in);
        
        UserDto dto = MapperFactory.getCopyByRefMapper()
                .mapClass(User.class, UserDto.class)
                .register()
                .map(u, UserDto.class);
        
        UserDto dto1 = BeanCopier.map(u, UserDto.class);
        System.out.println(dto1);
    }
    
}

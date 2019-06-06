package me.about.utils;

import com.baidu.unbiz.easymapper.MapperFactory;


public class BeanCopier {

    public  static <A,B> void map(A source,B target) {
         MapperFactory.getCopyByRefMapper()
        .mapClass(source.getClass(), target.getClass())
        .register()
        .map(source, target);
    }
    
    public static <A,B> B map(A source,Class<B> target) {
        return MapperFactory.getCopyByRefMapper()
        .mapClass(source.getClass(), target)
        .register()
        .map(source, target);
    }
}

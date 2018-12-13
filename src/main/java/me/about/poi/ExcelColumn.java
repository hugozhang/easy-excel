package me.about.poi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface ExcelColumn {
    // 列名
    String name() default "";

    // 宽度
    int width() default 30;
    
    // 必填
    boolean required() default false;
    
    String format() default "yyyy-MM-dd HH:mm";
}

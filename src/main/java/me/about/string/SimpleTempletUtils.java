package me.about.string;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimpleTempletUtils {

    public static String render(String template, Map<String, Object> context) {
        if(template == null || context.isEmpty()) return template;
        for (String s : context.keySet()) {
            template = template.replaceAll("\\$\\{".concat(s).concat("\\}"),
                    context.get(s) == null ? "" : context.get(s).toString());
        }
        return template;
    }

    public static void main(String[] args) {
        String template = "亲爱的用户${name},你好，上次登录时间为${time}";
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("name", "sad");
        context.put("time", new Date());
        System.out.println(render(template, context));
    }

}

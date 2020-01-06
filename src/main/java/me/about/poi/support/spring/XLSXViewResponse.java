package me.about.poi.support.spring;

import me.about.poi.writer.XLSXWriter;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

//三种解析方式 http://zeng233.github.io/2016/11/02/6.7spring%20MVC%E5%A4%84%E7%90%86Excel%E8%A7%86%E5%9B%BE%E7%9A%84%E4%B8%89%E7%A7%8D%E6%96%B9%E5%BC%8F/

/**
 * <mvc:annotation-driven>
 *   <mvc:message-converters>
 *     <bean class="me.about.poi.support.spring.XLSXViewResponse"/>
 *   </mvc:message-converters>
 * </mvc:annotation-driven>
 */
public class XLSXViewResponse implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotationUtils.findAnnotation(returnType.getContainingClass(), XLSXView.class) != null
                || returnType.getMethodAnnotation(XLSXView.class) != null);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        response.setContentType("application/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=download.xlsx");

        if (returnValue instanceof List) {
            XLSXWriter.builder().toStream((List)returnValue,response.getOutputStream());
        }
    }
}

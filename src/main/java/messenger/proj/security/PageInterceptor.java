package messenger.proj.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class PageInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Получение текущего URL-маппинга
        String currentMapping = request.getRequestURI();

        // Сохранение информации о текущем URL-маппинге, например, в сессии пользователя
        request.getSession().setAttribute("currentMapping", currentMapping);

        return true;
    }
}
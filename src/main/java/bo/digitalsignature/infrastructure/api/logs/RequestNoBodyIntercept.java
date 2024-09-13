package bo.digitalsignature.infrastructure.api.logs;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
@Component
public class RequestNoBodyIntercept implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        displayReq(request, null);
        displayResp(request, response, null);
        return true;
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for (String str : headerMap) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }

    public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
        if ("POST".equals(request.getMethod())
                || "PUT".equals(request.getMethod())
                || "GET".equals(request.getMethod())
                || "DELETE".equals(request.getMethod())
                || "PATH".equals(request.getMethod())) {
            return;
        }

        StringBuilder respMessage = new StringBuilder();
        Map<String, String> headersRequest = getHeaders(request);
        Map<String, String> headers = getHeaders(response);

        respMessage.append("-------------------RESPONSE - HTTP STATUS : " + response.getStatus() + " ----------------------\n");
        respMessage.append(" method = [").append(request.getMethod()).append("]").append(", \n");

        if (!headersRequest.isEmpty()) {
            respMessage.append(" RequestHeaders = [").append(headersRequest).append("]").append(", \n");
        }

        if (!headers.isEmpty()) {
            respMessage.append(" ResponseHeaders = [").append(headers).append("]").append(", \n");
        }

        respMessage.append("DATA ").append(", \n");
        respMessage.append(" responseBody = [").append(body).append("]").append(", \n");

        respMessage.append("------------------------------------------------\n");

        log.info(respMessage.toString());
    }

    public void displayReq(HttpServletRequest request, Object body) throws ServletException, IOException {
        if ("POST".equals(request.getMethod())
                || "PUT".equals(request.getMethod())
                || "PATH".equals(request.getMethod())) {
            return;
        }

        StringBuilder reqMessage = new StringBuilder();
        Map<String, String> parameters = getParameters(request);
        Map<String, String> headers = getHeaders(request);

        reqMessage.append("-------------------REQUEST----------------------\n");
        reqMessage.append("RemoteAddr = [").append(request.getRemoteAddr()).append("]").append(", \n");
        reqMessage.append("RemoteHost = [").append(request.getRemoteHost()).append("]").append(", \n");
        reqMessage.append("RemoteUser= [").append(request.getRemoteUser()).append("]").append(", \n");
        reqMessage.append("method = [").append(request.getMethod()).append("]").append(", \n");
        reqMessage.append(" URI = [").append(request.getRequestURI()).append("?").append(request.getQueryString()).append("] ").append(", \n");

        if (!headers.isEmpty()) {
            reqMessage.append(" ResponseHeaders = [").append(headers).append("]").append(", \n");
        }

        if (!parameters.isEmpty()) {
            reqMessage.append(" parameters = [").append(parameters).append("] ").append(", \n");
        }

        reqMessage.append("------------------------------------------------\n");

        log.info(reqMessage.toString());
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        Enumeration<String> headerMap = request.getHeaderNames();

        while (headerMap.hasMoreElements()) {
            String headerName = headerMap.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        return headers;
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }
}

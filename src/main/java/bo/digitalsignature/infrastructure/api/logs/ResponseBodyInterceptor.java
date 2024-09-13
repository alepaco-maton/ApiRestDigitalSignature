package bo.digitalsignature.infrastructure.api.logs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Log4j2
@Component
@ControllerAdvice
public class ResponseBodyInterceptor implements ResponseBodyAdvice<Object> {

    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse response;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        displayResp(this.request, this.response, body);

        return body;
    }

    private void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
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

        if (body instanceof Page) {
            Page page = (Page) body;
            respMessage.append(" responseBody = [").
                    append("totalPages=").append(page.getTotalPages()).
                    append("totalElements=").append(page.getTotalElements()).
                    append("size=").append(page.getSize()).
                    append("content=").append(page.getContent()).
                    append("]").append(", \n");
        } else {
            respMessage.append(" responseBody = [").append(body).append("]").append(", \n");
        }

        respMessage.append("------------------------------------------------\n");

        log.info(respMessage.toString());
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

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for (String str : headerMap) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }

}
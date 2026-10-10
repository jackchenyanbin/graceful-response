package com.feiniaojin.gracefulresponse.defaults;


import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * 提供的默认的ResponseBeanFactory实现.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 */
public class DefaultResponseFactory implements ResponseFactory {

    private final Logger logger = LoggerFactory.getLogger(DefaultResponseFactory.class);

    private static final Integer RESPONSE_STYLE_0 = 0;

    private static final Integer RESPONSE_STYLE_1 = 1;

    /**
     * 缓存自定义Response的构造器，避免每次响应都进行反射查找
     */
    private volatile Constructor<?> cachedConstructor;

    /**
     * cachedConstructor对应的类全限定名，用于配置变化时重新解析
     */
    private volatile String cachedConstructorClassName;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private GracefulResponseProperties properties;

    @Override
    public Response newEmptyInstance() {
        try {
            String responseClassFullName = properties.getResponseClassFullName();
            //配置了Response的全限定名，即自定义了Response，用配置的进行返回
            if (StringUtils.hasLength(responseClassFullName)) {
                return (Response) resolveConstructor(responseClassFullName).newInstance();
            } else {
                //没有配Response的全限定名，则创建DefaultResponse
                return generateDefaultResponse();
            }
        } catch (Exception e) {
            throw new GracefulResponseException("创建空的Response失败", e);
        }
    }

    /**
     * 解析并缓存自定义Response的无参构造器
     *
     * @param responseClassFullName Response类的全限定名
     * @return 缓存的构造器
     */
    private Constructor<?> resolveConstructor(String responseClassFullName)
            throws ClassNotFoundException, NoSuchMethodException {
        Constructor<?> constructor = this.cachedConstructor;
        if (constructor != null && responseClassFullName.equals(this.cachedConstructorClassName)) {
            return constructor;
        }
        constructor = Class.forName(responseClassFullName).getConstructor();
        this.cachedConstructor = constructor;
        this.cachedConstructorClassName = responseClassFullName;
        return constructor;
    }

    private Response generateDefaultResponse() {
        Integer responseStyle = properties.getResponseStyle();
        //默认的Response style，未配置responseStyle时默认使用Style1，responseStyle==0使用Style0，responseStyle==1使用Style1
        if (Objects.isNull(responseStyle)) {
            return new DefaultResponseImplStyle1();
        }
        if (RESPONSE_STYLE_0.equals(responseStyle)) {
            return new DefaultResponseImplStyle0();
        } else if (RESPONSE_STYLE_1.equals(responseStyle)) {
            return new DefaultResponseImplStyle1();
        } else {
            logger.error("不支持的Response style类型,responseStyle={}", responseStyle);
            throw new IllegalArgumentException("不支持的Response style类型");
        }
    }

    @Override
    public Response newInstance(ResponseStatus responseStatus) {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatus);
        return bean;
    }

    @Override
    public Response newInstance(ResponseStatus statusLine, Object data) {
        Response bean = this.newInstance(statusLine);
        bean.setPayload(data);
        return bean;
    }

    @Override
    public Response newSuccessInstance() {
        Response emptyInstance = this.newEmptyInstance();
        emptyInstance.setStatus(responseStatusFactory.defaultSuccess());
        return emptyInstance;
    }

    @Override
    public Response newSuccessInstance(Object payload) {
        Response bean = this.newSuccessInstance();
        bean.setPayload(payload);
        return bean;
    }

    @Override
    public Response newFailInstance() {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatusFactory.defaultError());
        return bean;
    }

}

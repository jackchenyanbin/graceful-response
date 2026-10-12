package com.feiniaojin.gracefulresponse.defaults;

/**
 * 默认的响应码和提示信息
 *
 * @author qinyujie
 */
public class DefaultConstants {

    private DefaultConstants() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 默认的成功响应码
     */
    public static final String DEFAULT_SUCCESS_CODE = "0";

    /**
     * 默认的成功提示信息
     */
    public static final String DEFAULT_SUCCESS_MSG = "ok";

    /**
     * 默认的错误码
     */
    public static final String DEFAULT_ERROR_CODE = "1";

    /**
     * 默认的错误提示
     */
    public static final String DEFAULT_ERROR_MSG = "error";

    /**
     * 发生错误时默认的HTTP状态码，用于配置缺失时的兜底
     */
    public static final int DEFAULT_HTTP_STATUS_CODE_ON_ERROR = 200;

}

package cc.dmji.api.common;

import java.util.ArrayList;

/**
 * 该类表示服务器的返回结果
 * 比如操作成功的返回结果为：
 * <p>
 *     {
 *     "code": 0,
 *     "msg": "OK",
 *     "data": "some data"
 *     }
 * </p>
 * @param <T> 返回数据的类型
 */
public class Result<T> {

    /*返回的状态码*/
    Integer code;

   /*返回的提示信息*/
    String msg;

    /*返回的数据*/
    T data;


    /**
     * 无参数构造器
     */
    public Result() {
        this.data = (T) new ArrayList<String>();
    }

    /**
     * 传入消息状态码初始化Result
     * @param resultCode 消息状态码
     */
    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    /**
     * 传入消息状态码和数据初始化Result
     * @param resultCode 消息状态码
     * @param data 返回的数据
     */
    public Result(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.data = data;
    }

    /**
     * 设置消息状态码
     * @param resultCode 消息状态码
     * @return 返回Result对象
     */
    public Result setResultCode(ResultCode resultCode){
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}

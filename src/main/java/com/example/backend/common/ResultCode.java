package com.example.backend.common;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS("000", "成功"),
    UNAUTHORIZED("401", "用户未授权，请登录"),
    FORBIDDEN("403","权限不足，请联系管理员添加权限"),
    PARAM_IS_INVALID("1001", "参数无效"),
    PARAM_IS_BLANK("1002", "参数为空"),
    PARAM_IS_FAIL("1003","参数校验失败"),
    TOKEN_IS_FAIL("1004", "token校验失败"),
    BODY_IS_FAIL("1005", "body不能为空"),
    HEADER_AREAID_IS_FAIL("1006", "非超管管理内容，必须在header中加入areaId，并且值不能为空"),

    ID_IS_BLANK("1100", "id不能为空"),
    CODE_IS_BLANK("1101", "code不能为空"),

    USER_NOT_LOGGED_IN("2001", "用户未登录"),
    USER_LOGIN_ERROR("2002", "用户不存在或密码错误"),
    USER_NOT_EXIST("2003", "用户不存在"),
    USER_HAS_EXISTED("2004", "用户已存在"),
    USER_NOT_DEPART("2005","用户不存在任何部门"),
    USER_NOT_ROLE("2006","用户不存在任何角色"),
    USER_NOT_AREA("2007","用户所在区域不存在"),
    DEPART_NOT_EXIST("2008", "部门不存在"),
    INSERT_FAIL("3001", "添加失败"),
    DELETE_FAIL("3002", "删除失败"),
    UPDATE_FAIL("3003", "更新失败"),
    QUERY_FAIL("3004", "查询为空"),
    AREA_FAIL("3005", "区域id为空"),
    DEVICE_NOT_EXIST("4001", "设备不存在"),
    DEVICE_NOT_SERVER("4002", "设备没有配置服务器信息"),
    EVENT_NOT_FINISH("6000", "事件相关部门未全部完成，无法完结"),
    RUNTIME_EXCEPTION("4000", "运行时异常"),
    METHOD_NOT_SUPPORT("5000", "请求方式不支持"),


    MDYH_IDCARD_IS_BLANK("8000", "身份证不能为空"),
    UNKNOW_EXCEPTION("500", "系统未知异常"),


    DATABASE_DATA_NOT_EXIST("10404", "数据不存在"),
    CONNECTION_EXCEPTION("H500", "连接海康异常");

    private String code;

    private String message;
}

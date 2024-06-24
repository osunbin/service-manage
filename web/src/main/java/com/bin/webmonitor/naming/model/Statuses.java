package com.bin.webmonitor.naming.model;

public class Statuses {
    public static int SUCCESS = 0;
    public static int SERVER_ERROR = -1;
    public static int NO_CALLER = 1; //没有调用方
    public static int NO_SERVICE = 2; //没有服务方
    public static int NO_CONFIG_CHANGE = 3; //配置没有改变
    public static int FORBID = 4; //服务方拒绝调用
    public static int NO_CALLER_USEAGE = 5; //没有调用关系
    public static int NO_GROUP = 6; //分组不存在
    public static int NO_SERVICE_FUNCTION = 7; //服务没有函数
    public static int IO_EXCEPTION = 8; //服务没有函数
    public static int SERVICE_NO_NODES = 9; //服务没有函数
    public static int SERVICE_NO_NODES_AFTER_CONTROL_FILTER = 10; //服务没有函数
}

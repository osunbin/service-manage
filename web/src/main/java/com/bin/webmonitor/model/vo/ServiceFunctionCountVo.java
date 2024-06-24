package com.bin.webmonitor.model.vo;

public class ServiceFunctionCountVo {
    private int id;
    private String name;
    private long count;

    public ServiceFunctionCountVo() {}

    public ServiceFunctionCountVo(int id, String name, long count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ServiceFunctionCountVo{" + "id=" + id + ", name='" + name + '\'' + ", count=" + count + '}';
    }
}

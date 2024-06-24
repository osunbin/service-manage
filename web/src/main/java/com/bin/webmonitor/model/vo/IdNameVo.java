package com.bin.webmonitor.model.vo;

public class IdNameVo {

    private Integer id;

    private String name;

    public IdNameVo(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdNameVo{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}

package com.dong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树节点，字段对齐前端 index.js 期望的结构（menuId/parentId/name/url/type/icon/orderNum/list）。
 */
public class MenuVO implements Serializable {

    private Integer menuId;
    private Long parentId;
    private String name;
    private String url;
    private String perms;
    private Integer type;
    private String icon;
    private Integer orderNum;
    private List<MenuVO> list;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public List<MenuVO> getList() {
        return list;
    }

    public void setList(List<MenuVO> list) {
        this.list = list;
    }
}

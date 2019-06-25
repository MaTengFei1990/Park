package com.hollysmart.db;

import java.io.Serializable;

/**
 * Created by Lenovo on 2019/4/17.
 */

public class UserInfo implements Serializable {


    private String access_token;
    private String userName;
    private String password;

    private String id;
    private String remarks;
    private String createDate;
    private String updateDate;
    private Office office;
    private String loginName;
    private String no;
    private String name;
    private String email;
    private String phone;
    private String mobile;
    private String userType;
    private String loginIp;
    private String loginDate;
    private String photo;
    private String oldLoginName;
    private String oldLoginIp;
    private String oldLoginDate;
    private String oauthId;
    private String userLevel;
    private String orderUnitId;
    private String roleIds;
    private String roleNames;


    private String restaskAdd;
    private String restaskDelete;
    private String restaskFinish;
    private String resdataUpdate;
    private String resdataDelete;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public Office getOffice() {
        return office;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getNo() {
        return no;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setOldLoginName(String oldLoginName) {
        this.oldLoginName = oldLoginName;
    }

    public String getOldLoginName() {
        return oldLoginName;
    }

    public void setOldLoginIp(String oldLoginIp) {
        this.oldLoginIp = oldLoginIp;
    }

    public String getOldLoginIp() {
        return oldLoginIp;
    }

    public void setOldLoginDate(String oldLoginDate) {
        this.oldLoginDate = oldLoginDate;
    }

    public String getOldLoginDate() {
        return oldLoginDate;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setOrderUnitId(String orderUnitId) {
        this.orderUnitId = orderUnitId;
    }

    public String getOrderUnitId() {
        return orderUnitId;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public class Office implements Serializable {

        private String id;
        private String remarks;
        private String createDate;
        private String updateDate;
        private String parentIds;
        private String name;
        private int sort;
        private Area area;
        private String code;
        private String type;
        private String grade;
        private String address;
        private String zipCode;
        private String master;
        private String phone;
        private String fax;
        private String email;
        private String useable;
        private String primaryPerson;
        private String deputyPerson;
        private String children;
        private String parentId;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setParentIds(String parentIds) {
            this.parentIds = parentIds;
        }

        public String getParentIds() {
            return parentIds;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getSort() {
            return sort;
        }

        public void setArea(Area area) {
            this.area = area;
        }

        public Area getArea() {
            return area;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getGrade() {
            return grade;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public String getMaster() {
            return master;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public String getFax() {
            return fax;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setUseable(String useable) {
            this.useable = useable;
        }

        public String getUseable() {
            return useable;
        }

        public void setPrimaryPerson(String primaryPerson) {
            this.primaryPerson = primaryPerson;
        }

        public String getPrimaryPerson() {
            return primaryPerson;
        }

        public void setDeputyPerson(String deputyPerson) {
            this.deputyPerson = deputyPerson;
        }

        public String getDeputyPerson() {
            return deputyPerson;
        }

        public void setChildren(String children) {
            this.children = children;
        }

        public String getChildren() {
            return children;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getParentId() {
            return parentId;
        }

    }


    public class Area implements Serializable {

        private String id;
        private String remarks;
        private String createDate;
        private String updateDate;
        private String parentIds;
        private String name;
        private int sort;
        private String code;
        private String type;
        private String parentId;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setParentIds(String parentIds) {
            this.parentIds = parentIds;
        }

        public String getParentIds() {
            return parentIds;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getSort() {
            return sort;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }


        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getParentId() {
            return parentId;
        }

    }

    public String getRestaskAdd() {
        return restaskAdd;
    }

    public void setRestaskAdd(String restaskAdd) {
        this.restaskAdd = restaskAdd;
    }

    public String getRestaskDelete() {
        return restaskDelete;
    }

    public void setRestaskDelete(String restaskDelete) {
        this.restaskDelete = restaskDelete;
    }

    public String getRestaskFinish() {
        return restaskFinish;
    }

    public void setRestaskFinish(String restaskFinish) {
        this.restaskFinish = restaskFinish;
    }

    public String getResdataUpdate() {
        return resdataUpdate;
    }

    public void setResdataUpdate(String resdataUpdate) {
        this.resdataUpdate = resdataUpdate;
    }

    public String getResdataDelete() {
        return resdataDelete;
    }

    public void setResdataDelete(String resdataDelete) {
        this.resdataDelete = resdataDelete;
    }
}


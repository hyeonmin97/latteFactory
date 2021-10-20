package kr.ac.dongyang.project.dto;

import com.google.gson.annotations.SerializedName;

public class CustomerDTO {
    @SerializedName("success")
    private boolean success;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("emCol1")
    private String emCol1;

    @SerializedName("emCol2")
    private String emCol2;

    @SerializedName("emCol3")
    private String emCol3;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmCol1() {
        return emCol1;
    }

    public void setEmCol1(String emCol1) {
        this.emCol1 = emCol1;
    }

    public String getEmCol2() {
        return emCol2;
    }

    public void setEmCol2(String emCol2) {
        this.emCol2 = emCol2;
    }

    public String getEmCol3() {
        return emCol3;
    }

    public void setEmCol3(String emCol3) {
        this.emCol3 = emCol3;
    }


    public CustomerDTO(boolean success, String id, String name, String phone, String emCol1) {
        this.success = success;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.emCol1 = emCol1;
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", emCol1='" + emCol1 + '\'' +
                '}';
    }

    public void clear(){
        success = false;
        id = name = phone = emCol1 = emCol2 = emCol3 = null;
    }

}

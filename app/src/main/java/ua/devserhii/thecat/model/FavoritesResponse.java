package ua.devserhii.thecat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class    FavoritesResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
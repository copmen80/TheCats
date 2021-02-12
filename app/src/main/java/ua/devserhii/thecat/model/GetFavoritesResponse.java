package ua.devserhii.thecat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetFavoritesResponse {

@SerializedName("created_at")
@Expose
private String createdAt;
@SerializedName("id")
@Expose
private Integer id;
@SerializedName("image")
@Expose
private ImageResponse image;
@SerializedName("image_id")
@Expose
private String imageId;
@SerializedName("sub_id")
@Expose
private String subId;
@SerializedName("user_id")
@Expose
private String userId;

public String getCreatedAt() {
return createdAt;
}

public void setCreatedAt(String createdAt) {
this.createdAt = createdAt;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public ImageResponse getImage() {
return image;
}

public void setImage(ImageResponse image) {
this.image = image;
}

public String getImageId() {
return imageId;
}

public void setImageId(String imageId) {
this.imageId = imageId;
}

public String getSubId() {
return subId;
}

public void setSubId(String subId) {
this.subId = subId;
}

public String getUserId() {
return userId;
}

public void setUserId(String userId) {
this.userId = userId;
}

}
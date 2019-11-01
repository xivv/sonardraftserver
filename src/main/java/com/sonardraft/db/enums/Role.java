package com.sonardraft.db.enums;

import com.google.gson.annotations.SerializedName;

public enum Role {
	@SerializedName("Jungle")
	JUNGLE, @SerializedName("Bottom")
	BOTTOM, @SerializedName("Support")
	SUPPORT, @SerializedName("Mid")
	MID, @SerializedName("Top")
	TOP;
}

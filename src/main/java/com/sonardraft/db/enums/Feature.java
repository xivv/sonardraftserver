package com.sonardraft.db.enums;

import com.google.gson.annotations.SerializedName;

public enum Feature {
	@SerializedName("CC")
	CC, @SerializedName("HARDCC")
	HARDCC, @SerializedName("ENGAGE")
	ENGAGE, @SerializedName("HARDENGAGE")
	HARDENGAGE, @SerializedName("POKE")
	POKE, @SerializedName("SPLITPUSH")
	SPLITPUSH, @SerializedName("WAVECLEAR")
	WAVECLEAR, @SerializedName("DUELLANT")
	DUELLANT, @SerializedName("ROAM")
	ROAM, @SerializedName("SUSTAIN")
	SUSTAIN, @SerializedName("ASSASINATION")
	ASSASINATION, @SerializedName("TANK")
	TANK
}

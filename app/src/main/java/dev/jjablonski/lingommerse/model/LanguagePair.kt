package dev.jjablonski.lingommerse.model

import android.os.Parcel
import android.os.Parcelable

data class LanguagePair(
    val original: String,
    val translation: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(original)
        parcel.writeString(translation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LanguagePair> {
        override fun createFromParcel(parcel: Parcel): LanguagePair {
            return LanguagePair(parcel)
        }

        override fun newArray(size: Int): Array<LanguagePair?> {
            return arrayOfNulls(size)
        }
    }
}
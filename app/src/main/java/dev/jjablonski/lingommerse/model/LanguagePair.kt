package dev.jjablonski.lingommerse.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "language_pairs")
data class LanguagePair(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val original: String,
    val translation: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
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

package dev.jjablonski.lingommerse.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(
    tableName = "language_pairs",
    foreignKeys = [ForeignKey(
        entity = LanguageList::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LanguagePair(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val original: String,
    val translation: String,
    val listId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(original)
        parcel.writeString(translation)
        parcel.writeInt(listId)
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

package org.daaya.daayalearningapp.exo.network.objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


@Entity
public class DaayaVideo implements Parcelable {
    @PrimaryKey
    @SerializedName("filename")
    @NotNull
    public String filename="";

    @ColumnInfo(name = "title")
    @SerializedName("title")
    public String title;

    @ColumnInfo(name = "author")
    @SerializedName("author")
    public String author;

    @ColumnInfo(name="description")
    @SerializedName("description")
    public String description;

    @ColumnInfo(name="classification")
    @SerializedName("classification")
    public String classification;

    @ColumnInfo(name="taxonomy")
    @SerializedName("taxonomy")
    public DaayaVideoTaxonomy taxonomy;

    protected DaayaVideo(Parcel in) {
        title = in.readString();
        author = in.readString();
        description = in.readString();
        filename = Objects.requireNonNull(in.readString());
        classification = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(description);
        dest.writeString(filename);
        dest.writeString(classification);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DaayaVideo> CREATOR = new Creator<DaayaVideo>() {
        @Override
        public DaayaVideo createFromParcel(Parcel in) {
            return new DaayaVideo(in);
        }

        @Override
        public DaayaVideo[] newArray(int size) {
            return new DaayaVideo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DaayaVideo)) return false;
        DaayaVideo that = (DaayaVideo) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(author, that.author) &&
                Objects.equals(description, that.description) &&
                Objects.equals(filename, that.filename) &&
                Objects.equals(classification, that.classification) &&
                Objects.equals(taxonomy, that.taxonomy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, description, filename, classification, taxonomy);
    }

    public DaayaVideo() {

    }
    public DaayaVideo(@NotNull String title, @NotNull String author, @NotNull String description,
                      @NotNull String filename,
                     @NotNull String classification, @NotNull DaayaVideoTaxonomy taxonomy) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.classification = classification;
        this.taxonomy = taxonomy;
        this.filename = filename;
    }
}

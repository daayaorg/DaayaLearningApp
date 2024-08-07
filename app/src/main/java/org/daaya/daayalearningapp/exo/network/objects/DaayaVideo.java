package org.daaya.daayalearningapp.exo.network.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class DaayaVideo implements Parcelable {
    @SerializedName("title")
    public String title;
    @SerializedName("author")
    public String author;
    @SerializedName("description")
    public String description;
    @SerializedName("filename")
    public String filename;
    @SerializedName("classification")
    public String classification;
    @SerializedName("taxonomy")
    public DaayaVideoTaxonomy taxonomy;

    protected DaayaVideo(Parcel in) {
        title = in.readString();
        author = in.readString();
        description = in.readString();
        filename = in.readString();
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
}

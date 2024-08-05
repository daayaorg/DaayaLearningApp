package org.daaya.daayalearningapp.exo.network.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class DaayaVideo {
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

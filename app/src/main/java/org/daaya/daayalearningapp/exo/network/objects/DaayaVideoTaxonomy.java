package org.daaya.daayalearningapp.exo.network.objects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/* the tree follows this hierarchy
      "class"
      "order"
      "family"
      "tribe"
      "genus"
 */
public class DaayaVideoTaxonomy {
    @SerializedName("class")
    public String clas;
    @SerializedName("order")
    public String order;
    @SerializedName("family")
    public String family;
    @SerializedName("tribe")
    public String tribe;
    @SerializedName("genus")
    public String genus;

    static public DaayaVideoTaxonomy fromString(String str){
        if (str.startsWith("/")){
            str = str.substring(1);
        }
        String[] parts = str.split("/");
        DaayaVideoTaxonomy taxonomy = new DaayaVideoTaxonomy();
        int len = parts.length;
        if (len >= 1)
            taxonomy.clas = parts[0];
        if (len >= 2)
            taxonomy.order = parts[1];
        if (len >= 3)
            taxonomy.family = parts[2];
        if (len >= 4)
            taxonomy.tribe = parts[3];
        if (len >= 5)
            taxonomy.genus = parts[4];

        return taxonomy;
    }

    @NonNull
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (clas != null)
            sb.append(clas);
        if (order != null)
            sb.append("/").append(order);
        if (family != null)
            sb.append("/").append(family);
        if (tribe != null)
            sb.append("/").append(tribe);
        if (genus != null)
            sb.append("/").append(genus);
        return sb.toString();
    }
}

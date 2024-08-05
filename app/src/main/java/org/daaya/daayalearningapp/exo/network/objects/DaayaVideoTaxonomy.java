package org.daaya.daayalearningapp.exo.network.objects;

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
}

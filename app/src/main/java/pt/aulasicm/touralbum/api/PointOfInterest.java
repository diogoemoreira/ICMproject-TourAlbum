package pt.aulasicm.touralbum.api;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class PointOfInterest {
    @SerializedName("kinds")
    private String kinds;
    @SerializedName("sources")
    private HashMap sources;   //?????????????????????
    @SerializedName("bbox")
    private HashMap bbox;
    @SerializedName("point") /////////////// (lat & long)
    private HashMap point;
    @SerializedName("osm")
    private String osm;
    @SerializedName("otm")
    private String otm;
    @SerializedName("xid")  ///////////////
    private String xid;
    @SerializedName("name") ///////////////
    private String name;
    @SerializedName("wikipedia")  ///////////////
    private String wikipedia_link;
    @SerializedName("image")
    private String image_link;
    @SerializedName("wikidata")
    private String wikidata;
    @SerializedName("rate")
    private String rate;
    @SerializedName("info")
    private HashMap info;


    //CONSTRUCTOR
    public PointOfInterest(String kind,HashMap sources,HashMap bbox, HashMap point,String osm,
                           String xid,String name,String wikipedia_link,String image_link,
                           String wikidata,String rate,HashMap info) {
        this.kinds = kinds;
        this.sources=sources;
        this.bbox=bbox;
        this.point=point;
        this.osm=osm;
        this.xid=xid;
        this.name=name;
        this.wikipedia_link=wikipedia_link;
        this.image_link=image_link;
        this.wikidata=wikidata;
        this.rate=rate;
        this.info=info;
    }



    //GETTERS & SETTERS
    public String getKinds() { return kinds; }
    public void setKinds(String kinds) { this.kinds = kinds; }
    public HashMap getSources() { return sources; }
    public void setSources(HashMap sources) { this.sources = sources; }
    public HashMap getBbox() { return bbox; }
    public void setBbox(HashMap bbox) { this.bbox = bbox; }
    public HashMap getPoint() { return point; }
    public void setPoint(HashMap point) { this.point = point; }
    public String getOsm() { return osm; }
    public void setOsm(String osm) { this.osm = osm; }
    public String getOtm() { return otm; }
    public void setOtm(String otm) { this.otm = otm; }
    public String getXid() { return xid; }
    public void setXid(String xid) { this.xid = xid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getWikipedia_link() { return wikipedia_link; }
    public void setWikipedia_link(String wikipedia_link) { this.wikipedia_link = wikipedia_link; }
    public String getImage_link() { return image_link; }
    public void setImage_link(String image_link) { this.image_link = image_link; }
    public String getWikidata() { return wikidata; }
    public void setWikidata(String wikidata) { this.wikidata = wikidata; }
    public String getRate() { return rate; }
    public void setRate(String rate) { this.rate = rate; }
    public HashMap getInfo() { return info; }
    public void setInfo(HashMap info) { this.info = info; }
}

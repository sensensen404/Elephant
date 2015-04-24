package org.idaxiang.elephant.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Azzssss on 15-1-2.
 */
public class FavModel extends DataSupport {

    private int id;
    private String nid;
    private String title;
    private String created;
    private String changed;
    private String summary;
    private String useruid;
    private String username;
    private String taxonomy;
    private boolean fav;

    public FavModel(String nid, String title, String created, String changed, String summary, String useruid, String username, String taxonomy, boolean fav) {
        this.nid = nid;
        this.title = title;
        this.created = created;
        this.changed = changed;
        this.summary = summary;
        this.useruid = useruid;
        this.username = username;
        this.taxonomy = taxonomy;
        this.fav = fav;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUseruid() {
        return useruid;
    }

    public void setUseruid(String useruid) {
        this.useruid = useruid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}

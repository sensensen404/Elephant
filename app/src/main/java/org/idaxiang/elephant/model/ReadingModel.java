package org.idaxiang.elephant.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Azzssss on 14-12-31.
 */
public class ReadingModel extends DataSupport {

    private String body;
    private String nid;
    private String related;
    private String annotation;
    private String useruid;
    private String username;
    private String usersignature;
    private String userpictuer;
    private String email;
    private String weibo;
    private String userweixin;
    private String mainimage;
    private String authorid;
    private String authorname;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
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

    public String getUsersignature() {
        return usersignature;
    }

    public void setUsersignature(String usersignature) {
        this.usersignature = usersignature;
    }

    public String getUserpictuer() {
        return userpictuer;
    }

    public void setUserpictuer(String userpictuer) {
        this.userpictuer = userpictuer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getUserweixin() {
        return userweixin;
    }

    public void setUserweixin(String userweixin) {
        this.userweixin = userweixin;
    }

    public String getMainimage() {
        return mainimage;
    }

    public void setMainimage(String mainimage) {
        this.mainimage = mainimage;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    @Override
    public String toString() {
        return "ReadingModel{" +
                "body='" + body + '\'' +
                ", nid='" + nid + '\'' +
                ", related='" + related + '\'' +
                ", annotation='" + annotation + '\'' +
                ", useruid='" + useruid + '\'' +
                ", username='" + username + '\'' +
                ", usersignature='" + usersignature + '\'' +
                ", userpictuer='" + userpictuer + '\'' +
                ", email='" + email + '\'' +
                ", weibo='" + weibo + '\'' +
                ", userweixin='" + userweixin + '\'' +
                ", mainimage='" + mainimage + '\'' +
                ", authorid='" + authorid + '\'' +
                ", authorname='" + authorname + '\'' +
                '}';
    }
}

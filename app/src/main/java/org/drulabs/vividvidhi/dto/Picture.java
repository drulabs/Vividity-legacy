package org.drulabs.vividvidhi.dto;

/**
 * Created by kaushald on 05/02/17.
 */

public class Picture implements Comparable<Picture> {

    private String picName;
    private long dateTaken;
    private int commentsCount;
    private int likesCount;
    private String photoCredit;
    private String relationshipWithPhotographer;
    private String picURL;
    private String thumbURL;

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getPhotoCredit() {
        return photoCredit;
    }

    public void setPhotoCredit(String photoCredit) {
        this.photoCredit = photoCredit;
    }

    public String getRelationshipWithPhotographer() {
        return relationshipWithPhotographer;
    }

    public void setRelationshipWithPhotographer(String relationshipWithPhotographer) {
        this.relationshipWithPhotographer = relationshipWithPhotographer;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Picture picture = (Picture) o;

        if (dateTaken != picture.dateTaken) return false;
        if (!picName.equals(picture.picName)) return false;
        if (!photoCredit.equals(picture.photoCredit)) return false;
        return relationshipWithPhotographer != null ? relationshipWithPhotographer.equals(picture
                .relationshipWithPhotographer) : picture.relationshipWithPhotographer == null;

    }

    @Override
    public int hashCode() {
        int result = picName.hashCode();
        result = 31 * result + (int) (dateTaken ^ (dateTaken >>> 32));
        result = 31 * result + photoCredit.hashCode();
        result = 31 * result + (relationshipWithPhotographer != null ?
                relationshipWithPhotographer.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Picture picture) {
        if (dateTaken > picture.getDateTaken()) {
            return -1;
        } else if (dateTaken < picture.getDateTaken()) {
            return 1;
        }
        return 0;
    }
}

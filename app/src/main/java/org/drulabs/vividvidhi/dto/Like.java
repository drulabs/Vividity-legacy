package org.drulabs.vividvidhi.dto;

/**
 * Created by kaushald on 24/02/17.
 */

public class Like implements Comparable<Like>{

    private String artifactId;
    private String likedById;
    private String likedByName;
    private long likedOn;
    private String likerPic;
    private String likesUniqueIdentifier;

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getLikedById() {
        return likedById;
    }

    public void setLikedById(String likedById) {
        this.likedById = likedById;
    }

    public String getLikedByName() {
        return likedByName;
    }

    public void setLikedByName(String likedByName) {
        this.likedByName = likedByName;
    }

    public long getLikedOn() {
        return likedOn;
    }

    public void setLikedOn(long likedOn) {
        this.likedOn = likedOn;
    }

    public String getLikerPic() {
        return likerPic;
    }

    public void setLikerPic(String likerPic) {
        this.likerPic = likerPic;
    }

    public String getLikesUniqueIdentifier() {
        return likesUniqueIdentifier;
    }

    public void setLikesUniqueIdentifier(String likesUniqueIdentifier) {
        this.likesUniqueIdentifier = likesUniqueIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Like like = (Like) o;

        if (likedOn != like.likedOn) return false;
        if (!artifactId.equals(like.artifactId)) return false;
        if (!likedById.equals(like.likedById)) return false;
        return likesUniqueIdentifier.equals(like.likesUniqueIdentifier);

    }

    @Override
    public int hashCode() {
        int result = artifactId.hashCode();
        result = 31 * result + likedById.hashCode();
        result = 31 * result + (int) (likedOn ^ (likedOn >>> 32));
        result = 31 * result + likesUniqueIdentifier.hashCode();
        return result;
    }

    @Override
    public int compareTo(Like like) {
        if (this.likedOn > like.getLikedOn()) {
            return -1;
        } else if (this.likedOn < like.getLikedOn()) {
            return 1;
        } else {
            return 0;
        }
    }
}

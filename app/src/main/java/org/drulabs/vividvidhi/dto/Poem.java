package org.drulabs.vividvidhi.dto;

/**
 * Created by kaushald on 10/02/17.
 */

public class Poem implements Comparable<Poem> {

    private String name;
    private int likesCount;
    private int commentsCount;
    private String imageName;
    private long publishDate;
    private String text;
    private String writtenBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWrittenBy() {
        return writtenBy;
    }

    public void setWrittenBy(String writtenBy) {
        this.writtenBy = writtenBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poem poem = (Poem) o;

        if (publishDate != poem.publishDate) return false;
        if (!name.equals(poem.name)) return false;
        if (imageName != null ? !imageName.equals(poem.imageName) : poem.imageName != null)
            return false;
        return writtenBy.equals(poem.writtenBy);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
        result = 31 * result + (int) (publishDate ^ (publishDate >>> 32));
        //result = 31 * result + text.hashCode();
        result = 31 * result + writtenBy.hashCode();
        return result;
    }

    @Override
    public int compareTo(Poem poem) {
        if (publishDate > poem.getPublishDate()) {
            return -1;
        } else if (publishDate < poem.getPublishDate()) {
            return 1;
        }
        return 0;
    }
}

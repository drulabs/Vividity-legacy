package org.drulabs.vividvidhi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.drulabs.vividvidhi.dto.Like;
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.dto.Poem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaushald on 15/02/17.
 */

public class DBHandler {

    private static DBHandler handler = null;

    private Context context;
    private SQLiteDatabase db;

    private DBHandler(@NonNull Context cxt) {
        this.context = cxt;
        ArtifactsDB picsDB = new ArtifactsDB(context);
        db = picsDB.getWritableDatabase();
    }

    public static DBHandler getHandle(Context cxt) {
        if (handler == null) {
            synchronized (DBHandler.class) {
                if (handler == null) {
                    handler = new DBHandler(cxt);
                }
            }
        }
        return handler;
    }

    public long addPic(String picKey, Picture picture) {

        ContentValues cv = new ContentValues();
        cv.put(ArtifactsDB.PIC_KEY, picKey);
        cv.put(ArtifactsDB.PIC_COMMENTS_COUNT, picture.getCommentsCount());
        cv.put(ArtifactsDB.PIC_CREDIT, picture.getPhotoCredit());
        cv.put(ArtifactsDB.PIC_DATE, picture.getDateTaken());
        cv.put(ArtifactsDB.PIC_LIKES_COUNT, picture.getLikesCount());
        cv.put(ArtifactsDB.PIC_NAME, picture.getPicName());
        cv.put(ArtifactsDB.PIC_RELATIONSHIP, picture.getRelationshipWithPhotographer());
        cv.put(ArtifactsDB.PIC_THUMB_URL, picture.getThumbURL());
        cv.put(ArtifactsDB.PIC_URL, picture.getPicURL());

        long rowId = 0;

        if (!picExists(picKey)) {
            rowId = db.insert(ArtifactsDB.TABLE_PICS, null, cv);
        } else {
            rowId = db.update(ArtifactsDB.TABLE_PICS, cv, ArtifactsDB.PIC_KEY + "=?",
                    new String[]{picKey});
        }
        return rowId;

    }

    public long addPoem(String key, Poem poem) {
        ContentValues cv = new ContentValues();
        cv.put(ArtifactsDB.POEM_KEY, key);
        cv.put(ArtifactsDB.POEM_NAME, poem.getName());
        cv.put(ArtifactsDB.POEM_COMMENTS_COUNT, poem.getCommentsCount());
        cv.put(ArtifactsDB.POEM_CREDIT, poem.getWrittenBy());
        cv.put(ArtifactsDB.POEM_DATE, poem.getPublishDate());
        cv.put(ArtifactsDB.POEM_IMAGE, poem.getImageName());
        cv.put(ArtifactsDB.POEM_LIKES_COUNT, poem.getLikesCount());
        cv.put(ArtifactsDB.POEM_TEXT, poem.getText());

        long rowId = 0;

        if (!poemExists(key)) {
            rowId = db.insert(ArtifactsDB.TABLE_POEMS, null, cv);
        } else {
            rowId = db.update(ArtifactsDB.TABLE_POEMS, cv, ArtifactsDB.POEM_KEY + "=?", new
                    String[]{key});
        }

        return rowId;
    }

    public void addPic(@NonNull Map<String, Picture> pictureMap) {
        for (Map.Entry<String, Picture> row : pictureMap.entrySet()) {
            addPic(row.getKey(), row.getValue());
        }
    }

    public void addPoem(@NonNull Map<String, Poem> poemMap) {
        for (Map.Entry<String, Poem> row : poemMap.entrySet()) {
            addPoem(row.getKey(), row.getValue());
        }
    }

    public int setLikedForPic(@NonNull String picKey, boolean liked) {
        ContentValues values = new ContentValues();
        values.put(ArtifactsDB.PIC_IS_LIKED, liked);

        int updateCount = db.update(ArtifactsDB.TABLE_PICS, values, ArtifactsDB.PIC_KEY + "=?"
                , new String[]{picKey});
        return updateCount;
    }

    public int setLikedForPoem(@NonNull String poemKey, boolean liked) {
        ContentValues values = new ContentValues();
        values.put(ArtifactsDB.POEM_IS_LIKED, liked);

        int updateCount = db.update(ArtifactsDB.TABLE_POEMS, values, ArtifactsDB.POEM_KEY + "=?"
                , new String[]{poemKey});
        return updateCount;
    }

    public void setLikedForPic(@NonNull Map<String, Picture> picMap) {
        for (Map.Entry<String, Picture> row : picMap.entrySet()) {
            setLikedForPic(row.getKey(), true);
        }
    }

    public void setLikedForPoem(@NonNull Map<String, Poem> poemMap) {
        for (Map.Entry<String, Poem> row : poemMap.entrySet()) {
            setLikedForPoem(row.getKey(), true);
        }
    }

    public boolean isPicLiked(String picKey) {

        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_PICS, new String[]{ArtifactsDB.PIC_IS_LIKED},
                    ArtifactsDB.PIC_KEY + "=?", new String[]{picKey}, null, null, null);

            boolean isLiked = false;

            if (cursor.moveToNext()) {
                isLiked = cursor.getInt(cursor.getColumnIndex(ArtifactsDB.PIC_IS_LIKED)) == 1;
            }

            return isLiked;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;

    }

    public boolean isPoemLiked(String poemKey) {

        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_POEMS, new String[]{ArtifactsDB.POEM_IS_LIKED},
                    ArtifactsDB.POEM_KEY + "=?", new String[]{poemKey}, null, null, null);

            boolean isLiked = false;

            if (cursor.moveToNext()) {
                isLiked = cursor.getInt(cursor.getColumnIndex(ArtifactsDB.POEM_IS_LIKED)) == 1;
            }

            return isLiked;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;

    }

    public Map<String, Picture> getLikedPics() {
        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_PICS, null, ArtifactsDB.PIC_IS_LIKED + "=1", null,
                    null, null, ArtifactsDB.PIC_DATE + " DESC");

            int picKeyIndex = cursor.getColumnIndex(ArtifactsDB.PIC_KEY);
            int commentCountIndex = cursor.getColumnIndex(ArtifactsDB.PIC_COMMENTS_COUNT);
            int picCreditIndex = cursor.getColumnIndex(ArtifactsDB.PIC_CREDIT);
            int picDateIndex = cursor.getColumnIndex(ArtifactsDB.PIC_DATE);
            int likesCountIndex = cursor.getColumnIndex(ArtifactsDB.PIC_LIKES_COUNT);
            int picNameIndex = cursor.getColumnIndex(ArtifactsDB.PIC_NAME);
            int relationshipIndex = cursor.getColumnIndex(ArtifactsDB.PIC_RELATIONSHIP);
            int picUrlIndex = cursor.getColumnIndex(ArtifactsDB.PIC_URL);
            int thumbIndex = cursor.getColumnIndex(ArtifactsDB.PIC_THUMB_URL);

            Map<String, Picture> pictureMap = new HashMap<>();

            while (cursor.moveToNext()) {

                String picKey = cursor.getString(picKeyIndex);
                int commentsCount = cursor.getInt(commentCountIndex);
                String picCredit = cursor.getString(picCreditIndex);
                long date = cursor.getLong(picDateIndex);
                int likesCount = cursor.getInt(likesCountIndex);
                String picName = cursor.getString(picNameIndex);
                String relationship = cursor.getString(relationshipIndex);
                String picUrl = cursor.getString(picUrlIndex);
                String thumbUrl = cursor.getString(thumbIndex);

                Picture soloPic = new Picture();
                soloPic.setLikesCount(likesCount);
                soloPic.setPicName(picName);
                soloPic.setRelationshipWithPhotographer(relationship);
                soloPic.setPhotoCredit(picCredit);
                soloPic.setCommentsCount(commentsCount);
                soloPic.setDateTaken(date);
                soloPic.setPicURL(picUrl);
                soloPic.setThumbURL(thumbUrl);

                pictureMap.put(picKey, soloPic);
            }

            return pictureMap;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public Map<String, Poem> getLikedPoems() {
        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_POEMS, null, ArtifactsDB.POEM_IS_LIKED + "=1", null,
                    null, null, ArtifactsDB.POEM_DATE + " DESC");

            int poemKeyIndex = cursor.getColumnIndex(ArtifactsDB.POEM_KEY);
            int commentCountIndex = cursor.getColumnIndex(ArtifactsDB.POEM_COMMENTS_COUNT);
            int poemCreditIndex = cursor.getColumnIndex(ArtifactsDB.POEM_CREDIT);
            int poemDateIndex = cursor.getColumnIndex(ArtifactsDB.POEM_DATE);
            int likesCountIndex = cursor.getColumnIndex(ArtifactsDB.POEM_LIKES_COUNT);
            int poemNameIndex = cursor.getColumnIndex(ArtifactsDB.POEM_NAME);
            int poemImageIndex = cursor.getColumnIndex(ArtifactsDB.POEM_IMAGE);
            int poemTextIndex = cursor.getColumnIndex(ArtifactsDB.POEM_TEXT);

            Map<String, Poem> poemMap = new HashMap<>();

            while (cursor.moveToNext()) {

                String poemKey = cursor.getString(poemKeyIndex);
                int commentsCount = cursor.getInt(commentCountIndex);
                String poemCredit = cursor.getString(poemCreditIndex);
                long date = cursor.getLong(poemDateIndex);
                int likesCount = cursor.getInt(likesCountIndex);
                String poemName = cursor.getString(poemNameIndex);
                String poemImage = cursor.getString(poemImageIndex);
                String poemText = cursor.getString(poemTextIndex);

                Poem singlePoem = new Poem();
                singlePoem.setLikesCount(likesCount);
                singlePoem.setName(poemName);
                singlePoem.setWrittenBy(poemCredit);
                singlePoem.setImageName(poemImage);
                singlePoem.setCommentsCount(commentsCount);
                singlePoem.setPublishDate(date);
                singlePoem.setText(poemText);

                poemMap.put(poemKey, singlePoem);
            }

            return poemMap;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public Map<String, Picture> getAllPics() {
        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_PICS, null, null, null, null, null, ArtifactsDB
                    .PIC_DATE + " DESC");

            int picKeyIndex = cursor.getColumnIndex(ArtifactsDB.PIC_KEY);
            int commentCountIndex = cursor.getColumnIndex(ArtifactsDB.PIC_COMMENTS_COUNT);
            int picCreditIndex = cursor.getColumnIndex(ArtifactsDB.PIC_CREDIT);
            int picDateIndex = cursor.getColumnIndex(ArtifactsDB.PIC_DATE);
            int likesCountIndex = cursor.getColumnIndex(ArtifactsDB.PIC_LIKES_COUNT);
            int picNameIndex = cursor.getColumnIndex(ArtifactsDB.PIC_NAME);
            int relationshipIndex = cursor.getColumnIndex(ArtifactsDB.PIC_RELATIONSHIP);
            int picUrlIndex = cursor.getColumnIndex(ArtifactsDB.PIC_URL);
            int thumbIndex = cursor.getColumnIndex(ArtifactsDB.PIC_THUMB_URL);

            Map<String, Picture> pictureMap = new HashMap<>();

            while (cursor.moveToNext()) {

                String picKey = cursor.getString(picKeyIndex);
                int commentsCount = cursor.getInt(commentCountIndex);
                String picCredit = cursor.getString(picCreditIndex);
                long date = cursor.getLong(picDateIndex);
                int likesCount = cursor.getInt(likesCountIndex);
                String picName = cursor.getString(picNameIndex);
                String relationship = cursor.getString(relationshipIndex);
                String picUrl = cursor.getString(picUrlIndex);
                String thumbUrl = cursor.getString(thumbIndex);

                Picture soloPic = new Picture();
                soloPic.setLikesCount(likesCount);
                soloPic.setPicName(picName);
                soloPic.setRelationshipWithPhotographer(relationship);
                soloPic.setPhotoCredit(picCredit);
                soloPic.setCommentsCount(commentsCount);
                soloPic.setDateTaken(date);
                soloPic.setPicURL(picUrl);
                soloPic.setThumbURL(thumbUrl);

                pictureMap.put(picKey, soloPic);
            }

            return pictureMap;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public Map<String, Poem> getAllPoems() {
        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_POEMS, null, null, null, null, null, ArtifactsDB
                    .POEM_DATE + " DESC");

            int poemKeyIndex = cursor.getColumnIndex(ArtifactsDB.POEM_KEY);
            int commentCountIndex = cursor.getColumnIndex(ArtifactsDB.POEM_COMMENTS_COUNT);
            int poemCreditIndex = cursor.getColumnIndex(ArtifactsDB.POEM_CREDIT);
            int poemDateIndex = cursor.getColumnIndex(ArtifactsDB.POEM_DATE);
            int likesCountIndex = cursor.getColumnIndex(ArtifactsDB.POEM_LIKES_COUNT);
            int poemNameIndex = cursor.getColumnIndex(ArtifactsDB.POEM_NAME);
            int poemImageIndex = cursor.getColumnIndex(ArtifactsDB.POEM_IMAGE);
            int poemTextIndex = cursor.getColumnIndex(ArtifactsDB.POEM_TEXT);

            Map<String, Poem> poemMap = new HashMap<>();

            while (cursor.moveToNext()) {

                String poemKey = cursor.getString(poemKeyIndex);
                int commentsCount = cursor.getInt(commentCountIndex);
                String poemCredit = cursor.getString(poemCreditIndex);
                long date = cursor.getLong(poemDateIndex);
                int likesCount = cursor.getInt(likesCountIndex);
                String poemName = cursor.getString(poemNameIndex);
                String poemImage = cursor.getString(poemImageIndex);
                String poemText = cursor.getString(poemTextIndex);

                Poem singlePoem = new Poem();
                singlePoem.setLikesCount(likesCount);
                singlePoem.setName(poemName);
                singlePoem.setWrittenBy(poemCredit);
                singlePoem.setImageName(poemImage);
                singlePoem.setCommentsCount(commentsCount);
                singlePoem.setPublishDate(date);
                singlePoem.setText(poemText);

                poemMap.put(poemKey, singlePoem);
            }

            return poemMap;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public Picture getPic(String picKey) {
        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_PICS, null, ArtifactsDB.PIC_KEY + "=?",
                    new String[]{picKey}, null, null, ArtifactsDB.PIC_DATE + " DESC");

            int picKeyIndex = cursor.getColumnIndex(ArtifactsDB.PIC_KEY);
            int commentCountIndex = cursor.getColumnIndex(ArtifactsDB.PIC_COMMENTS_COUNT);
            int picCreditIndex = cursor.getColumnIndex(ArtifactsDB.PIC_CREDIT);
            int picDateIndex = cursor.getColumnIndex(ArtifactsDB.PIC_DATE);
            int likesCountIndex = cursor.getColumnIndex(ArtifactsDB.PIC_LIKES_COUNT);
            int picNameIndex = cursor.getColumnIndex(ArtifactsDB.PIC_NAME);
            int relationshipIndex = cursor.getColumnIndex(ArtifactsDB.PIC_RELATIONSHIP);
            int picUrlIndex = cursor.getColumnIndex(ArtifactsDB.PIC_URL);
            int thumbIndex = cursor.getColumnIndex(ArtifactsDB.PIC_THUMB_URL);

            Picture soloPic = null;

            if (cursor.moveToNext()) {
                int commentsCount = cursor.getInt(commentCountIndex);
                String picCredit = cursor.getString(picCreditIndex);
                long date = cursor.getLong(picDateIndex);
                int likesCount = cursor.getInt(likesCountIndex);
                String picName = cursor.getString(picNameIndex);
                String relationship = cursor.getString(relationshipIndex);
                String picUrl = cursor.getString(picUrlIndex);
                String thumbUrl = cursor.getString(thumbIndex);

                soloPic = new Picture();
                soloPic.setLikesCount(likesCount);
                soloPic.setPicName(picName);
                soloPic.setRelationshipWithPhotographer(relationship);
                soloPic.setPhotoCredit(picCredit);
                soloPic.setCommentsCount(commentsCount);
                soloPic.setDateTaken(date);
                soloPic.setPicURL(picUrl);
                soloPic.setThumbURL(thumbUrl);
            }

            return soloPic;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public Poem getPoem(String poemKey) {
        Cursor cursor = null;

        try {
            cursor = db.query(ArtifactsDB.TABLE_POEMS, null, ArtifactsDB.POEM_KEY + "=?", new
                    String[]{poemKey}, null, null, ArtifactsDB.POEM_DATE + " DESC");

            int commentCountIndex = cursor.getColumnIndex(ArtifactsDB.POEM_COMMENTS_COUNT);
            int poemCreditIndex = cursor.getColumnIndex(ArtifactsDB.POEM_CREDIT);
            int poemDateIndex = cursor.getColumnIndex(ArtifactsDB.POEM_DATE);
            int likesCountIndex = cursor.getColumnIndex(ArtifactsDB.POEM_LIKES_COUNT);
            int poemNameIndex = cursor.getColumnIndex(ArtifactsDB.POEM_NAME);
            int poemImageIndex = cursor.getColumnIndex(ArtifactsDB.POEM_IMAGE);
            int poemTextIndex = cursor.getColumnIndex(ArtifactsDB.POEM_TEXT);

            Poem singlePoem = null;

            if (cursor.moveToNext()) {

                int commentsCount = cursor.getInt(commentCountIndex);
                String poemCredit = cursor.getString(poemCreditIndex);
                long date = cursor.getLong(poemDateIndex);
                int likesCount = cursor.getInt(likesCountIndex);
                String poemName = cursor.getString(poemNameIndex);
                String poemImage = cursor.getString(poemImageIndex);
                String poemText = cursor.getString(poemTextIndex);

                singlePoem = new Poem();
                singlePoem.setLikesCount(likesCount);
                singlePoem.setName(poemName);
                singlePoem.setWrittenBy(poemCredit);
                singlePoem.setImageName(poemImage);
                singlePoem.setCommentsCount(commentsCount);
                singlePoem.setPublishDate(date);
                singlePoem.setText(poemText);
            }

            return singlePoem;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public void updateLikedArtifacts(List<Like> likeList) {

        for (Like like : likeList) {
            setLikedForPoem(like.getArtifactId(), true);
            setLikedForPic(like.getArtifactId(), true);
        }

    }

    public boolean picExists(String picKey) {
        boolean exists = false;

        Cursor cursor = db.query(ArtifactsDB.TABLE_PICS, null, ArtifactsDB.PIC_KEY + "=?", new
                String[]{picKey}, null, null, ArtifactsDB.PIC_NAME);

        exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

    public boolean poemExists(String poemKey) {
        boolean exists = false;

        Cursor cursor = db.query(ArtifactsDB.TABLE_POEMS, null, ArtifactsDB.POEM_KEY + "=?", new
                String[]{poemKey}, null, null, null);

        exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

}

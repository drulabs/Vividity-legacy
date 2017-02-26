package org.drulabs.ttl;

import android.widget.Button;

/**
 * Created by kaushald on 03/02/17.
 */

public class Builder {

    private Button button;
    private int styleResourceId;

    private static Builder builder;

    private Builder() {

    }

    public static Builder getInstance() {
        if (builder == null) {
            builder = new Builder();
        }
        return builder;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public Button getButton() {
        return this.button;
    }

    public void setStyleResourceId(int styleResourceId) {
        this.styleResourceId = styleResourceId;
    }

    public int getStyleResourceId() {
        return styleResourceId;
    }
}

package org.drulabs.ttl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LibActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);

        RelativeLayout holder = (RelativeLayout) findViewById(R.id.activity_lib);

        //Button button = new Button(new ContextThemeWrapper(this, Builder.getInstance()
        //        .getStyleResourceId()));
        Button button = Builder.getInstance().getButton();

        ((ViewGroup)button.getParent()).removeView(button);

        button.setText("Holy crab");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        button.setLayoutParams(params);

        holder.addView(button);



    }
}

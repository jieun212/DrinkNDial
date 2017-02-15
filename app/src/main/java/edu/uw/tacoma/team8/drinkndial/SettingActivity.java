package edu.uw.tacoma.team8.drinkndial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This class is base activity for SettingFragment and SettingFavoriteLocation fragment.
 *
 * @version 02/14/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
}

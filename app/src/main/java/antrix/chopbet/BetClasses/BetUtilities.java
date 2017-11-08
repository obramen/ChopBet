package antrix.chopbet.BetClasses;


import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import antrix.chopbet.R;

public class BetUtilities extends BaseActivity {

    Context context;
    String title;

    public BetUtilities(){

    }


    public void loadActionbar(String title){

        final ActionBar abar = getSupportActionBar();
        //abar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(title);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        //abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);

    }

}

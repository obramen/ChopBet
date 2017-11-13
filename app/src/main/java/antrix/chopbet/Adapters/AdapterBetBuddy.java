package antrix.chopbet.Adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBetBuddy extends ArrayAdapter<BetBuddy> implements View.OnClickListener{

    private ArrayList<BetBuddy> dataSet;
    Context mContext;
    FirebaseFirestore fireDbRef;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView addFriend;
        CircleImageView profileImage;
    }

    public AdapterBetBuddy(ArrayList<BetBuddy> data, Context context) {
        super(context, R.layout.list_friends, data);
        this.dataSet = data;
        this.mContext = context;
        this.fireDbRef = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object = getItem(position);
        BetBuddy betBuddy = (BetBuddy) object;

        switch (v.getId())
        {
            case R.id.addFriend:
                //Snackbar.make(v, "UserName is " + betBuddy.getUserName(), Snackbar.LENGTH_LONG)
                  //      .setAction("No action", null).show();
                //Toast.makeText(mContext, "Request sent", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BetBuddy betBuddy = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_friends, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.addFriend = (TextView) convertView.findViewById(R.id.addFriend);
            viewHolder.profileImage = (CircleImageView) convertView.findViewById(R.id.profileImage);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.name.setText(betBuddy.getUserName());
        viewHolder.addFriend.setText(betBuddy.getPhoneNumber());


        if (Objects.equals(viewHolder.addFriend.getText().toString(), "Add Friend")){
            viewHolder.addFriend.setTypeface(null, Typeface.NORMAL);
            viewHolder.addFriend.setEnabled(true);
        }else if (Objects.equals(viewHolder.addFriend.getText().toString(), "Sent")){
            viewHolder.addFriend.setTypeface(null, Typeface.ITALIC);
            viewHolder.addFriend.setEnabled(false);
        }else if (Objects.equals(viewHolder.addFriend.getText().toString(), "Pending")){
            viewHolder.addFriend.setTypeface(null, Typeface.ITALIC);
            viewHolder.addFriend.setEnabled(false);
        }


        viewHolder.addFriend.setOnClickListener(this);
        viewHolder.addFriend.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
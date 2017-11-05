package antrix.chopbet.BetClasses;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import antrix.chopbet.R;


public class ConfirmDialog {

    public Dialog dialog;
    Context context;
    String header;
    String message;
    String accept;
    String cancel;

    TextView confirmHeader;
    TextView confirmMessage;
    public TextView confirmAccept;
    TextView confirmCancel;

    public ConfirmDialog(){
    }

    public void NewConfirmDialog(Context context, String header, String message, String accept, String cancel){

        this.context = context;
        this.header = header;
        this.message = message;
        this.accept = accept;
        this.cancel = cancel;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setTitle(header);

        confirmHeader = (TextView) dialog.findViewById(R.id.confirmHeader);
        confirmMessage = (TextView) dialog.findViewById(R.id.confirmMessage);
        confirmAccept = (TextView) dialog.findViewById(R.id.confirmAccept);
        confirmCancel = (TextView) dialog.findViewById(R.id.confirmCancel);


        confirmHeader.setText(header);
        confirmMessage.setText(message);
        confirmCancel.setText(cancel);
        confirmAccept.setText(accept);



        this.confirmCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }



}

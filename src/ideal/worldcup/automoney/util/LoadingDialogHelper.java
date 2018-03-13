package ideal.worldcup.automoney.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import ideal.worldcup.automoney.R;

public class LoadingDialogHelper {
    private static LoadingDialogHelper current;

    private Context context;
    private Dialog dialog;

    public static LoadingDialogHelper getInstance(Context context) {
        if (current == null) {
            current = new LoadingDialogHelper();
        }

        current.setContext(context);
        return current;
    }

    private LoadingDialogHelper() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void show() {
        if (dialog == null) {
            dialog = new Dialog(this.context, R.style.loading_dialog_style);
        }

        ProgressBar pb = new ProgressBar(this.context);
        try{
            if (dialog != null) {
                dialog.addContentView(pb,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                dialog.setCancelable(false);
                dialog.show();
            }
        }catch (Exception e){

        }
    }

    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

package com.materialnotes.widget;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.materialnotes.R;

import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;

/**
 * Dialogo que muestra información acerca de aplicación.
 *
 * @author Daniel Pedraza Arcega
 */
public class AboutNoticeDialog extends RoboDialogFragment {

    private static final String TAG = AboutNoticeDialog.class.getSimpleName();

    @InjectView(R.id.version_text) private TextView versionText;

    /**
     * Crea la vista de este dialogo.
     *
     * @param inflater un inflater.
     * @param container un container.
     * @param savedInstanceState un bundle.
     * @return la vista.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_about_notice, container, false);
    }

    /**
     * Inicializa la vista creada.
     *
     * @param view la vista creada.
     * @param savedInstanceState un bundle.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            versionText.setText(getString(R.string.version_format, versionName));
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e(TAG, "Couldn't find version name", ex);
        }
    }
}
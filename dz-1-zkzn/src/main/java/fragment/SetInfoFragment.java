package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.zkzn.dz_1.MainActivity;
import com.example.zkzn.dz_1.R;

/**
 * Created by user on 2019/3/26.
 */

public class SetInfoFragment extends Fragment {
    private View mView;
    EditText idEdit=null;
    EditText nameEdit=null;
    EditText locationEdit=null;
    EditText companyEdit =null;
    EditText ratedSpeedEdit =null;
    EditText ratedLoadEdit =null;

    EditText supplementEdit =null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.setinfo_fragment, container, false);
        idEdit= mView.findViewById(R.id.liftid);
        nameEdit = mView.findViewById(R.id.operatorname);
        locationEdit = mView.findViewById(R.id.location);
        companyEdit = mView.findViewById(R.id.company);
        ratedSpeedEdit = mView.findViewById(R.id.ratedSpeedEdit);
        ratedLoadEdit = mView.findViewById(R.id.ratedLoadEdit);
        supplementEdit = mView.findViewById(R.id.supplement);
        loadConfig();
        return mView;
    }
    public void onSave(View view)
    {
        //获取控件id值

        String strID = idEdit.getText().toString();
        String strName = nameEdit.getText().toString();
        String strLocation = locationEdit.getText().toString();
        String strCompany = companyEdit.getText().toString();
        String strRatedSpeed = ratedSpeedEdit.getText().toString();
        String strRatedLoad = ratedLoadEdit.getText().toString();
        String strSupplement = supplementEdit.getText().toString();
        SharedPreferences sp = this.getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("liftid", strID);
        editor.putString("operator", strName);
        editor.putString("location", strLocation);
        editor.putString("company", strCompany);
        editor.putString("ratedSpeed", strRatedSpeed);
        editor.putString("ratedLoad", strRatedLoad);
        editor.putString("supplement", strSupplement);
        editor.commit();

        MainActivity.s_mLiftId = strID;
        MainActivity.s_mOperator = strName;
        MainActivity.s_mLocation = strLocation;
        MainActivity.s_mCompany = strCompany;
        MainActivity.s_mRatedSpeed = strRatedSpeed;
        MainActivity.s_mRatedLoad = strRatedLoad;
        MainActivity.s_mSupplement = strSupplement;


    }

    public void onCancel(View view)
    {

    }
    public void loadConfig()
    {
        idEdit.setText(MainActivity.s_mLiftId);
        nameEdit.setText(MainActivity.s_mOperator);
        locationEdit.setText(MainActivity.s_mLocation);
        companyEdit.setText(MainActivity.s_mCompany);
        supplementEdit.setText(MainActivity.s_mSupplement);
    }
}

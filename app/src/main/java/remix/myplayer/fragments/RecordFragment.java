package remix.myplayer.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import remix.myplayer.R;
import remix.myplayer.activities.RecordShareActivity;
import remix.myplayer.services.MusicService;

/**
 * Created by Remix on 2015/12/28.
 */
public class RecordFragment extends Fragment{
    private EditText mEdit;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record,null);
        mEdit = (EditText)rootView.findViewById(R.id.edit_record);
//        mEdit.getBackground().setColorFilter(getResources().getColor(R.color.cursor_color), PorterDuff.Mode.SRC_ATOP);
        mEdit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mEdit.getText().toString().equals("")){
                    Toast.makeText(getContext(),"请输入分享内容",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getActivity(), RecordShareActivity.class);
                Bundle arg = new Bundle();
                arg.putString("Content",mEdit.getText().toString());
                arg.putSerializable("MP3Info", MusicService.getCurrentMP3());
                intent.putExtras(arg);
                startActivity(intent);
                return true;
            }
        });
        return rootView;
    }

}


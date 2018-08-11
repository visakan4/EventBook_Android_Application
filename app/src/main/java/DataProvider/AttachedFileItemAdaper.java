package DataProvider;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventbookapp.R;

import Models.MetadataFile;

/**
 * Created by jebes on 11/27/2017.
 */

/**
 * Customised Attached File Item Adapter
 *
 * Adapter to show the list of items
 *
 */

public class AttachedFileItemAdaper extends ArrayAdapter<MetadataFile> {
    Context mContext;
    int mLayoutResourceId;

    public AttachedFileItemAdaper(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mContext = context;
        mLayoutResourceId = resource;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public MetadataFile getItem(int position) {
        return super.getItem(position);
    }


    @NonNull
    @Override

    /*
        Method Name : getView
        Functionalites: To show the list of files
     */

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentView = convertView;
        MetadataFile currentItem = getItem(position);
        if(currentView==null)
        {

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            currentView = inflater.inflate(mLayoutResourceId, parent,false);
        }
        currentView.setTag(currentItem);

        final CheckBox chckBoxItem = (CheckBox) currentView.findViewById(R.id.attachFileItem);
        TextView txtView = (TextView) currentView.findViewById(R.id.rowText);
        ImageView imageView = (ImageView) currentView.findViewById(R.id.AddImage);
        if(currentItem.getMetadata().getTitle().endsWith(".txt"))
        {
            imageView.setBackgroundResource(R.drawable.ic_txt);
        }else if(currentItem.getMetadata().getTitle().endsWith(".png"))
        {
            imageView.setBackgroundResource(R.drawable.ic_image);
        }else if(currentItem.getMetadata().getTitle().endsWith(".pdf"))
        {
            imageView.setBackgroundResource(R.drawable.ic_pdf);
        }

        txtView.setText(currentItem.getMetadata().getTitle());
        chckBoxItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked)
            {
                    getItem(position).setMarkedToDelete(isChecked);
            }
        });

        return currentView;
    }
}

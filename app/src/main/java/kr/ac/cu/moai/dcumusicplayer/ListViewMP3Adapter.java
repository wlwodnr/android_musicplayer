package kr.ac.cu.moai.dcumusicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;

public class ListViewMP3Adapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    public ListViewMP3Adapter(Context context, ArrayList<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        TextView text1 = rowView.findViewById(android.R.id.text1);
        TextView text2 = rowView.findViewById(android.R.id.text2);

        String filePath = values.get(position);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);

        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String duration = getDuration(retriever);

        text1.setText(title != null ? title : "Unknown Title");
        text2.setText((artist != null ? artist : "Unknown Artist") + " - " + duration);

        // IOException 처리를 위한 try-catch 블록 추가
        try {
            retriever.release();
        } catch (IOException e) {
            e.printStackTrace(); // 예외 처리
        }

        return rowView;
    }

    public static String getDuration(MediaMetadataRetriever retriever) {
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);
        long minutes = (dur / 1000) / 60;
        long seconds = (dur / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

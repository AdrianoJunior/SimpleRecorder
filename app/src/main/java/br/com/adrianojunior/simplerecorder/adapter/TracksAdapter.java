package br.com.adrianojunior.simplerecorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Date;

import br.com.adrianojunior.simplerecorder.R;

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TracksViewHolder> {

    private File[] allTracks;

    private onTrackClick onTrackClick;

    public TracksAdapter(File[] allTracks, onTrackClick onTrackClick) {
        this.allTracks = allTracks;
        this.onTrackClick = onTrackClick;
    }

    @NonNull
    @Override
    public TracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracks_row, parent, false);
        return new TracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TracksViewHolder holder, final int position) {

        holder.setFileName(allTracks[position].getName());

        Date lastModified = new Date(allTracks[position].lastModified());
        holder.setLastModified(android.text.format.DateFormat
                .format("dd/MM/yyyy" + "\n" + "HH:mm", lastModified)
                .toString());


    }

    @Override
    public int getItemCount() {
        return allTracks.length;
    }

    public class TracksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mView;

        private ImageView listImage;
        private TextView fileNameTxt;
        private TextView lastModifiedTxt;


        public TracksViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            listImage = mView.findViewById(R.id.tracks_image);

            fileNameTxt = mView.findViewById(R.id.file_name_txt);

            lastModifiedTxt = mView.findViewById(R.id.last_modified_txt);

            itemView.setOnClickListener(this);
        }

        public void setFileName(String fileName) {
            fileNameTxt.setText(fileName);
        }

        public void setLastModified(String lastModified){

            lastModifiedTxt.setText(lastModified);

        }

        @Override
        public void onClick(View v) {
            onTrackClick.onClickListener(allTracks[getAdapterPosition()], getAdapterPosition());
        }
    }

    public interface onTrackClick {

        void onClickListener(File file, int position);
    }
}

package com.example.emda.soundrecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by emda on 2/11/2018.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.RecordingsViewHolder>
        implements DatabaseChangeListener{


    private DatabaseHelperMethod mDatabase;

    RecordedItem item;
    Context mContext;
    LinearLayoutManager linearLayoutManager;

    public FilesAdapter(Context context, LinearLayoutManager linearLayoutManager) {
        super();
        mContext = context;
        mDatabase = new DatabaseHelperMethod(mContext);
        mDatabase.setOnDatabaseChangedListener(this);
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

        item = getItem(position);
        long itemDuration = item.getLength();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(item.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        mContext,
                        item.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlaybackFragment playbackFragment =
                            new PlaybackFragment().init(getItem(holder.getPosition()));

                    FragmentTransaction transaction = ((FragmentActivity) mContext)
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");

                } catch (Exception e) {
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add(mContext.getString(R.string.share));
                entrys.add(mContext.getString(R.string.rename));
                entrys.add(mContext.getString(R.string.delete));

                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);


                // File delete confirm
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.options);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            shareFileDialog(holder.getPosition());
                        } if (item == 1) {
                            renameFileDialog(holder.getPosition());
                        } else if (item == 2) {
                            deleteFileDialog(holder.getPosition());
                        }
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                return false;
            }
        });
    }

    @Override
    public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.recorded_item_card_view, parent, false);

        mContext = parent.getContext();

        return new RecordingsViewHolder(itemView);
    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;

        public RecordingsViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.file_name_tv);
            vLength = (TextView) v.findViewById(R.id.file_length_tv);
            vDateAdded = (TextView) v.findViewById(R.id.file_created_date_tv);
            cardView = v.findViewById(R.id.card_view);
        }
    }

    @Override
    public int getItemCount() {
        return mDatabase.getCount();
    }

    public RecordedItem getItem(int position) {
        return mDatabase.getItemAtPosition(position);
    }

    @Override
    public void DatabaseEntryAdded() {
        //item added to top of the list
        notifyItemInserted(getItemCount() - 1);
        linearLayoutManager.scrollToPosition(getItemCount() - 1);
    }

    @Override
    //TODO
    public void DatabaseEntryRenamed() {

    }

    public void removeFile(int position) {
        //removeFile item from recyclerview, storage and database

        File file = new File(getItem(position).getFilePath());
        file.delete();

        Toast.makeText(
                mContext,
                String.format(
                        mContext.getString(R.string.deleted_string),
                        getItem(position).getName()
                ),
                Toast.LENGTH_SHORT
        ).show();

        mDatabase.removeItemWithId(getItem(position).getId());
        notifyItemRemoved(position);
    }



    public void renameFile(int position, String name) {
        //renameFile a file

        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += "/VoiceRecorder/" + name;
        File f = new File(mFilePath);

        if (f.exists() && !f.isDirectory()) {
            //file name is not unique, cannot renameFile file.
            Toast.makeText(mContext,
                    String.format(mContext.getString(R.string.file_already_exit_text), name),
                    Toast.LENGTH_SHORT).show();

        } else {
            //file name is unique, renameFile file
            File oldFilePath = new File(getItem(position).getFilePath());
            oldFilePath.renameTo(f);
            mDatabase.renameRecord(getItem(position), name, mFilePath);
            notifyItemChanged(position);
        }
    }


    public void shareFileDialog(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getItem(position).getFilePath())));
        shareIntent.setType("audio/mp4");
        mContext.startActivity(Intent.createChooser(shareIntent,mContext.getString(R.string.share_with)));
    }

    public void renameFileDialog (final int position) {
        // File renameFile dialog
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rename_dailog_layout, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle(R.string.rename_file);
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String value = input.getText().toString().trim() + ".mp4";
                            renameFile(position, value);

                        } catch (Exception e) {
                        }

                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    public void deleteFileDialog (final int position) {
        // File delete confirm
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(mContext);
        confirmDelete.setTitle(R.string.delete_file);
        confirmDelete.setMessage(R.string.delete_file_text);
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            removeFile(position);

                        } catch (Exception e) {
                        }

                        dialog.cancel();
                    }
                });
        confirmDelete.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = confirmDelete.create();
        alert.show();
    }
}

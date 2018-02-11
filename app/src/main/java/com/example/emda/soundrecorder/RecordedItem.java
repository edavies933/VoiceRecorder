package com.example.emda.soundrecorder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emda on 2/11/2018.
 */

public class RecordedItem implements Parcelable {
    private int fileId; //file id in local database
    private String fileName; // file name
    private int fileLength; // length of recording file in seconds
    private long fileTime; // date/time of the recording file
    private String filePath; //file path

    public RecordedItem() {
    }

    public RecordedItem(Parcel in) {
        fileName = in.readString();
        filePath = in.readString();
        fileId = in.readInt();
        fileLength = in.readInt();
        fileTime = in.readLong();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLength() {
        return fileLength;
    }

    public void setLength(int length) {
        fileLength = length;
    }

    public int getId() {
        return fileId;
    }

    public void setId(int id) {
        fileId = id;
    }

    public String getName() {
        return fileName;
    }

    public void setName(String name) {
        fileName = name;
    }

    public long getTime() {
        return fileTime;
    }

    public void setTime(long time) {
        fileTime = time;
    }

    public static final Parcelable.Creator<RecordedItem> CREATOR = new Parcelable.Creator<RecordedItem>() {
        public RecordedItem createFromParcel(Parcel in) {
            return new RecordedItem(in);
        }

        public RecordedItem[] newArray(int size) {
            return new RecordedItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(fileId);
        dest.writeInt(fileLength);
        dest.writeLong(fileTime);
        dest.writeString(filePath);
        dest.writeString(fileName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}

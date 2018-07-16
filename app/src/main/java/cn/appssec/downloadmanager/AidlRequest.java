package cn.appssec.downloadmanager;

import android.os.Parcel;
import android.os.Parcelable;



public class AidlRequest implements Parcelable {
    private String uri;
    private String destinationUri;
    private String mimeType;

    public AidlRequest(String uri, String destinationUri, String mimeType) {
        this.uri = uri;
        this.destinationUri = destinationUri;
        this.mimeType = mimeType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDestinationUri() {
        return destinationUri;
    }

    public void setDestinationUri(String destinationUri) {
        this.destinationUri = destinationUri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "AidlRequest{" +
                "uri='" + uri + '\'' +
                ", destinationUri='" + destinationUri + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uri);
        dest.writeString(this.destinationUri);
        dest.writeString(this.mimeType);
    }

    protected AidlRequest(Parcel in) {
        this.uri = in.readString();
        this.destinationUri = in.readString();
        this.mimeType = in.readString();
    }

    public static final Parcelable.Creator<AidlRequest> CREATOR = new Parcelable.Creator<AidlRequest>() {
        @Override
        public AidlRequest createFromParcel(Parcel source) {
            return new AidlRequest(source);
        }

        @Override
        public AidlRequest[] newArray(int size) {
            return new AidlRequest[size];
        }
    };
}

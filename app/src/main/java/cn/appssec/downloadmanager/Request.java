package cn.appssec.downloadmanager;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Request implements Parcelable {
    public static final int NETWORK_MOBILE = 1 << 0;
    public static final int NETWORK_WIFI = 1 << 1;
    @Deprecated
    public static final int NETWORK_BLUETOOTH = 1 << 2;
    private Uri mUri;
    private Uri mDestinationUri;
    private List<Pair<String, String>> mRequestHeaders = new ArrayList<Pair<String, String>>();
    private CharSequence mTitle;
    private CharSequence mDescription;
    private String mMimeType;
    private int mAllowedNetworkTypes = ~0; // default to all network types allowed
    private boolean mRoamingAllowed = true;
    private boolean mMeteredAllowed = true;
    private int mFlags = 0;
    private boolean mIsVisibleInDownloadsUi = true;
    private boolean mScannable = false;
    private boolean mUseSystemCache = false;

    private static final int SCANNABLE_VALUE_YES = 0;
    private static final int SCANNABLE_VALUE_NO = 2;
    public static final int VISIBILITY_VISIBLE = 0;
    public static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1;
    public static final int VISIBILITY_HIDDEN = 2;
    public static final int VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION = 3;
    private int mNotificationVisibility = VISIBILITY_VISIBLE;

    public Request(Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new IllegalArgumentException("Can only download HTTP/HTTPS URIs: " + uri);
        }
        mUri = uri;
    }
    Request(String uriString) {
        mUri = Uri.parse(uriString);
    }

    protected Request(Parcel in) {
        mUri = in.readParcelable(Uri.class.getClassLoader());
        mDestinationUri = in.readParcelable(Uri.class.getClassLoader());
        mMimeType = in.readString();
        mAllowedNetworkTypes = in.readInt();
        mRoamingAllowed = in.readByte() != 0;
        mMeteredAllowed = in.readByte() != 0;
        mFlags = in.readInt();
        mIsVisibleInDownloadsUi = in.readByte() != 0;
        mScannable = in.readByte() != 0;
        mUseSystemCache = in.readByte() != 0;
        mNotificationVisibility = in.readInt();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public Request setDestinationUri(Uri uri) {
        mDestinationUri = uri;
        return this;
    }
    public Uri getDestinationUri(){
        return mDestinationUri;
    }
    public Request setDestinationToSystemCache() {
        mUseSystemCache = true;
        return this;
    }
    public Request setDestinationInExternalFilesDir(Context context, String dirType,
                                                    String subPath) {
        final File file = context.getExternalFilesDir(dirType);
        if (file == null) {
            throw new IllegalStateException("Failed to get external storage files directory");
        } else if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalStateException(file.getAbsolutePath() +
                        " already exists and is not a directory");
            }
        } else {
            if (!file.mkdirs()) {
                throw new IllegalStateException("Unable to create directory: "+
                        file.getAbsolutePath());
            }
        }
        setDestinationFromBase(file, subPath);
        return this;
    }
    public Request setDestinationInExternalPublicDir(String dirType, String subPath) {
        File file = Environment.getExternalStoragePublicDirectory(dirType);
        if (file == null) {
            throw new IllegalStateException("Failed to get external storage public directory");
        } else if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalStateException(file.getAbsolutePath() +
                        " already exists and is not a directory");
            }
        } else {
            if (!file.mkdirs()) {
                throw new IllegalStateException("Unable to create directory: "+
                        file.getAbsolutePath());
            }
        }
        setDestinationFromBase(file, subPath);
        return this;
    }

    private void setDestinationFromBase(File base, String subPath) {
        if (subPath == null) {
            throw new NullPointerException("subPath cannot be null");
        }
        mDestinationUri = Uri.withAppendedPath(Uri.fromFile(base), subPath);
    }
    public void allowScanningByMediaScanner() {
        mScannable = true;
    }
    public Request addRequestHeader(String header, String value) {
        if (header == null) {
            throw new NullPointerException("header cannot be null");
        }
        if (header.contains(":")) {
            throw new IllegalArgumentException("header may not contain ':'");
        }
        if (value == null) {
            value = "";
        }
        mRequestHeaders.add(Pair.create(header, value));
        return this;
    }
    public Request setTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    public Request setDescription(CharSequence description) {
        mDescription = description;
        return this;
    }
    public Request setMimeType(String mimeType) {
        mMimeType = mimeType;
        return this;
    }
    @Deprecated
    public Request setShowRunningNotification(boolean show) {
        return (show) ? setNotificationVisibility(VISIBILITY_VISIBLE) :
                setNotificationVisibility(VISIBILITY_HIDDEN);
    }
    public Request setNotificationVisibility(int visibility) {
        mNotificationVisibility = visibility;
        return this;
    }
    public Request setAllowedNetworkTypes(int flags) {
        mAllowedNetworkTypes = flags;
        return this;
    }
    public Request setAllowedOverRoaming(boolean allowed) {
        mRoamingAllowed = allowed;
        return this;
    }
    public Request setAllowedOverMetered(boolean allow) {
        mMeteredAllowed = allow;
        return this;
    }
    public Request setRequiresCharging(boolean requiresCharging) {
        if (requiresCharging) {
            mFlags |= Downloads.Impl.FLAG_REQUIRES_CHARGING;
        } else {
            mFlags &= ~Downloads.Impl.FLAG_REQUIRES_CHARGING;
        }
        return this;
    }
    public Request setRequiresDeviceIdle(boolean requiresDeviceIdle) {
        if (requiresDeviceIdle) {
            mFlags |= Downloads.Impl.FLAG_REQUIRES_DEVICE_IDLE;
        } else {
            mFlags &= ~Downloads.Impl.FLAG_REQUIRES_DEVICE_IDLE;
        }
        return this;
    }
    public Request setVisibleInDownloadsUi(boolean isVisible) {
        mIsVisibleInDownloadsUi = isVisible;
        return this;
    }
    ContentValues toContentValues(String packageName) {
        ContentValues values = new ContentValues();
        assert mUri != null;
        values.put(Downloads.Impl.COLUMN_URI, mUri.toString());
        values.put(Downloads.Impl.COLUMN_IS_PUBLIC_API, true);
        values.put(Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE, packageName);

        if (mDestinationUri != null) {
            values.put(Downloads.Impl.COLUMN_DESTINATION, Downloads.Impl.DESTINATION_FILE_URI);
            values.put(Downloads.Impl.COLUMN_FILE_NAME_HINT, mDestinationUri.toString());
        } else {
            values.put(Downloads.Impl.COLUMN_DESTINATION,
                    (this.mUseSystemCache) ?
                            Downloads.Impl.DESTINATION_SYSTEMCACHE_PARTITION :
                            Downloads.Impl.DESTINATION_CACHE_PARTITION_PURGEABLE);
        }
        // is the file supposed to be media-scannable?
        values.put(Downloads.Impl.COLUMN_MEDIA_SCANNED, (mScannable) ? SCANNABLE_VALUE_YES :
                SCANNABLE_VALUE_NO);

        if (!mRequestHeaders.isEmpty()) {
            encodeHttpHeaders(values);
        }

        putIfNonNull(values, Downloads.Impl.COLUMN_TITLE, mTitle);
        putIfNonNull(values, Downloads.Impl.COLUMN_DESCRIPTION, mDescription);
        putIfNonNull(values, Downloads.Impl.COLUMN_MIME_TYPE, mMimeType);

        values.put(Downloads.Impl.COLUMN_VISIBILITY, mNotificationVisibility);
        values.put(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES, mAllowedNetworkTypes);
        values.put(Downloads.Impl.COLUMN_ALLOW_ROAMING, mRoamingAllowed);
        values.put(Downloads.Impl.COLUMN_ALLOW_METERED, mMeteredAllowed);
        values.put(Downloads.Impl.COLUMN_FLAGS, mFlags);
        values.put(Downloads.Impl.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI, mIsVisibleInDownloadsUi);

        return values;
    }

    private void encodeHttpHeaders(ContentValues values) {
        int index = 0;
        for (Pair<String, String> header : mRequestHeaders) {
            String headerString = header.first + ": " + header.second;
            values.put(Downloads.Impl.RequestHeaders.INSERT_KEY_PREFIX + index, headerString);
            index++;
        }
    }

    private void putIfNonNull(ContentValues contentValues, String key, Object value) {
        if (value != null) {
            contentValues.put(key, value.toString());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mUri, flags);
        dest.writeParcelable(mDestinationUri, flags);
        dest.writeString(mMimeType);
        dest.writeInt(mAllowedNetworkTypes);
        dest.writeByte((byte) (mRoamingAllowed ? 1 : 0));
        dest.writeByte((byte) (mMeteredAllowed ? 1 : 0));
        dest.writeInt(mFlags);
        dest.writeByte((byte) (mIsVisibleInDownloadsUi ? 1 : 0));
        dest.writeByte((byte) (mScannable ? 1 : 0));
        dest.writeByte((byte) (mUseSystemCache ? 1 : 0));
        dest.writeInt(mNotificationVisibility);
    }
}

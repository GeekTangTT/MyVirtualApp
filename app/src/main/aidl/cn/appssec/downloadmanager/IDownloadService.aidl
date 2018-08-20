// IDownloadService.aidl
package cn.appssec.downloadmanager;
import cn.appssec.downloadmanager.AidlRequest;
// Declare any non-default types here with import statements
import android.net.Uri;
//import android.database.Cursor;
import android.os.ParcelFileDescriptor;
import java.lang.String;
import android.content.Context;
import cn.appssec.downloadmanager.Request;
interface IDownloadService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   //long enqueue(in AidlRequest request);
      long enqueue(in Request request);

   Uri getDownloadUri(long id);

   Uri getUriForDownloadedFile(long id);


//
   //int remove(long[] ids);
//
   //Cursor query(in IQuery query);
//
   ParcelFileDescriptor openDownloadedFile(long id);
//
   String getMimeTypeForDownloadedFile(long id);

   //long getMaxBytesOverMobile(Context context);

   //long getRecommendedMaxBytesOverMobile(Context context);

//   long addCompletedDownload(String title, String description,
//               boolean isMediaScannerScannable, String mimeType, String path, long length,
//               boolean showNotification);

//   long addCompletedDownload(String title, String description,
//               boolean isMediaScannerScannable, String mimeType, String path, long length,
//               boolean showNotification, Uri uri, Uri referer);
//
   //void validateArgumentIsNonEmpty(String paramName, String val);

   //String getWhereClauseForIds(long[] ids);

//   String[] getWhereArgsForIds(long[] ids);
//
//   String[] getWhereArgsForIds(long[] ids, String[] args);


}

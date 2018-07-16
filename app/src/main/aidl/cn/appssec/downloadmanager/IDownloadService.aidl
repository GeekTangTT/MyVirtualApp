// IDownloadService.aidl
package cn.appssec.downloadmanager;
import cn.appssec.downloadmanager.AidlRequest;
// Declare any non-default types here with import statements
import android.net.Uri;
interface IDownloadService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   long enqueue(in AidlRequest request);

   Uri getDownloadUri(long id);

   Uri getUriForDownloadedFile(long id);
}

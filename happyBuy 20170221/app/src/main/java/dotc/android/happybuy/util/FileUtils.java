package dotc.android.happybuy.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import dotc.android.happybuy.GlobalContext;

/**
 * 文件操作工具类
 *
 * @author youdongbao
 * @date 2014年12月16日
 */

public class FileUtils {
    private static final String TAG = "SZU_FileUtils";
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static final String EX_IMAGE = "image";

    /**
     * 判断SD是否存在
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获得图片SD卡路径
     */
    public static String getPicSDPath(){
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = isSdcardExist();
        if (sdCardExist) {
            // 获取跟目录
//            sdDir = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES);
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString()+"/DCIM";
        }
        return null;
    }

    public static File newMemoryFile(String type){
        return new File(getExFileDir(type), UUID.randomUUID().toString()+".jpg");
    }

    public static File getExFileDir(String type) {
        return GlobalContext.get().getExternalFilesDir(type);
    }

    /**
     * 获得SD卡路径
     *
     * @param
     * @return String
     */
    public static String getSDPath() {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = isSdcardExist();
        if (sdCardExist) {
            // 获取跟目录
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString();
        }
        return null;
    }

    /**
     * 文件信息获取相关操作
     * <p/>
     * * /
     * <p/>
     * /** 通过判断文件是否存在
     *
     * @param path
     * @return
     */

    public static boolean isFileExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取sdcard目录下的文件
     * @param fileName
     * @return
     */
    public static String loadFromExternalFile(String fileName) {
        String result = "";
        InputStreamReader reader = null;
        try {
            File file = new File(getExternalStroageDirectory(), fileName);
            if (file.exists()){
                reader = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader);
                String lineTXT = "";
                while ((lineTXT = bufferedReader.readLine()) != null) {
                    result+=lineTXT.toString();
                }
            }
        } catch (Exception e) {
        }
        finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
    /**
     * 获取文件的Uri
     *
     * @param path 文件的路径
     * @return
     */
    public static Uri getUriFromFile(String path) {
        File file = new File(path);
        return Uri.fromFile(file);
    }

    /**
     * 通过路径获得文件父路径
     *
     * @param
     * @return
     */
    public static String getFileDirectory(String filepath) {
        if (null == filepath) {
            return null;
        }
        int dot = filepath.lastIndexOf("/");
        if (dot >= 0) {
            return filepath.substring(0, dot + 1);
        } else {
            return null;
        }
    }

    /**
     * 通过路径获得文件名字
     *
     * @param
     * @return
     */
    public static String getFileName(String filepath) {
        if (null == filepath) {
            return null;
        }
        int dot = filepath.lastIndexOf(File.separator);
        if (dot >= 0) {
            return filepath.substring(dot + 1);
        } else {
            return filepath;
        }
    }

    /**
     * 文件后最
     *
     * @param uri
     * @return
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            return "";
        }
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        try {
            if (path == null) {
                return 0;
            }
            File file = new File(path);
            if (file != null && file.exists() && file.length() > 0) {
                return file.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    /**
     * 换算文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("##0.0");
        String fileSize = "";
        double KB = 1024;
        double MB = 1024 * KB;
        double GB = 1024 * MB;
        if (size < KB) {
            fileSize = df.format((double) size) + "B";
        } else if (size < MB) {
            fileSize = df.format(size / KB) + "K";
        } else if (size < GB) {
            fileSize = df.format(size / MB) + "M";
        } else {
            fileSize = df.format(size / GB) + "G";
        }
        return fileSize;
    }

    /**
     * 文件创建、复制、删除、移动相关操作
     * <p/>
     * * /
     * <p/>
     * /** 创建文件
     *
     * @param path 文件路径
     * @return 创建的文件
     */
    public static File createFile(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            if (f.getParentFile() != null && !f.getParentFile().exists()) {
                if (f.getParentFile().mkdirs()) {
                    f.createNewFile();
                }
            } else {
                f.createNewFile();
            }
        }
        return f;
    }

    /**
     * 创建目录 父路径需要存在
     *
     * @param path 目录路径
     */
    public static void createDirFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 删除文件 包括子文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; ++i) {
                    deleteFile(files[i]);
                }
            }
        }
        return file.delete();
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        boolean bDelete = false;

        if (path != null && path.length() > 0) {
            try {
                File file = new File(path);
                bDelete = deleteFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bDelete;
    }

    /**
     * 文件拷贝
     *
     * @param src
     * @param dst
     * @return
     */
    public static boolean copyFile(File src, File dst) {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        if (src.isFile()) {
            try {
                if (dst.exists()) {
                    dst.delete();
                }
                fos = new FileOutputStream(dst);
                fis = new FileInputStream(src);
                int len = 0;
                byte[] buffer = new byte[1024 * 4];
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        if (src.isDirectory()) {
            dst.mkdir();
            File[] f = src.listFiles();
            if (f != null){
                for (int i = 0; i < f.length; i++) {
                    copyFile(f[i].getAbsoluteFile(), new File(dst.getAbsoluteFile() + File.separator + f[i].getName()));
                }
            }
        }
        return true;
    }

    /**
     * 移动文件
     *
     * @param src
     * @param tar
     * @return
     * @throws Exception
     */
    public static boolean moveFile(File src, File tar) throws Exception {
        if (copyFile(src, tar)) {
            deleteFile(src);
            return true;
        }
        return false;
    }

    /**
     * 保存图片到文件
     *
     * @param bitmap
     * @param file
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static boolean writeBitmapToFile(Bitmap bitmap, String file) throws IOException {
        if (bitmap == null || file == null) {
            return false;
        }
        OutputStream out = null;
        try {
            FileOutputStream stream = new FileOutputStream(file);
            out = new BufferedOutputStream(stream, 1 * 1024 * 1024);
            return bitmap.compress(CompressFormat.JPEG, 70, out);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 保存字节数据到文件
     *
     * @param filePath
     * @param data
     * @param appendDate
     * @return
     */
    public static boolean writeBytesToFile(String filePath, byte[] data, boolean appendDate) {
        boolean result = false;
        if (filePath == null || filePath.length() == 0 || data == null || data.length == 0) {
            return result;
        }
        if (appendDate) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.CHINA);
            Date date = new Date(System.currentTimeMillis());
            String timeStr = sdf.format(date);
            filePath = filePath + "." + timeStr;
        }
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            File parent = file.getParentFile();
            if (parent.exists() == false) {
                parent.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            result = true;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 将file转成二进制文件。<br/>
     *
     * @param file 需要转化的问价。<br/>
     **/
    public static byte[] readFileToBytes(File file) {
        if (!file.exists())
            return null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                Log.e(TAG, "File is too large");
                return null;
            }

            // Create the byte array to hold the data
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
                offset += numRead;

            // Ensure all the bytes have been read in
            if (offset < bytes.length)
                Log.d(TAG, "Could not completely read file " + file);
            return bytes;
        } catch (IOException e) {
            Log.d(TAG, "Failed to read file " + file);
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写文件
     *
     * @param fullPath
     * @param data
     * @return
     */
    public static boolean writeFile(String fullPath, String data) {
        String filePath = getFileDirectory(fullPath);
        String fileName = getFileName(fullPath);
        return writeFile(filePath, fileName, data);
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param fileName
     * @param
     * @return
     */
    public static boolean writeFile(String filePath, String fileName, String data) {

        FileOutputStream out = null;
        File wfileDir = new File(filePath);
        String newFileName = fileName.toString();
        if (fileName != null) {
            newFileName = fileName.toString().replaceAll("[\\\\/*?<>:\"|]", "");
        }
        File wfile = new File(filePath + newFileName);

        boolean ret = true;

        if (!wfileDir.exists()) {
            ret = wfileDir.mkdirs();
        }
        if (wfileDir.exists()) {
            if (!wfile.exists()) {
                try {
                    wfile.createNewFile();
                } catch (IOException e) {
                    ret = false;
                }
            }
            try {
                out = new FileOutputStream(wfile, false);
            } catch (FileNotFoundException e) {
                ret = false;
            }
            try {
                data = data + "\r\n";
                if (out != null)
                    out.write(data.getBytes());
            } catch (IOException e) {
                ret = false;
            }
            try {
                if (out != null)
                    out.flush();
            } catch (IOException e) {
                ret = false;
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static String readFile(String path) {
        boolean exists = isFileExists(path);
        if (!exists) {
            Log.d(TAG, "file no fount:" + path);
            return null;
        }
        File file = new File(path);
        return readFile(file);
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        String content = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);
            long length = file.length();
            int count = 0;
            byte[] data = new byte[2048];
            while (count < length) {
                int len = fis.read(data);
                baos.write(data, 0, len);
                count += len;
            }
            data = baos.toByteArray();
            content = new String(data, "utf-8");
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 应用存储
     */

    private static boolean isExistDataCache(Context context, String cachefile) {
        boolean exist = false;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 保存String到应用空间
     *
     * @param context
     * @param data
     * @param file
     * @param encode  编码方式
     * @return
     */
    public static boolean saveString(Context context, String data, String file, String encode) {
        if (TextUtils.isEmpty(data)) {
            return false;
        }

        if (TextUtils.isEmpty(encode)) {
            encode = DEFAULT_CHARSET;
        }

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(data.getBytes(encode));
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean isPrivateFileExists(Context ctx, String file) {
        return ctx.getFileStreamPath(file) != null && ctx.getFileStreamPath(file).exists();
    }

    public static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return !(file == null || !file.exists());
    }

    /**
     * 读取String从应用空间
     *
     * @param context
     * @param file
     * @return
     */
    public static String readString(Context context, String file, String encode) {
        if (TextUtils.isEmpty(encode)) {
            encode = DEFAULT_CHARSET;
        }
        String result = null;
        FileInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            is = context.openFileInput(file);
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            result = os.toString(encode);
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 保存Object到应用空间
     *
     * @param context
     * @param ser
     * @param file
     * @return
     */
    public static boolean saveObject(Context context, Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取Object从应用空间
     *
     * @param context
     * @param file
     * @return
     */
    public static Serializable readObject(Context context, String file) {
        if (!isExistDataCache(context, file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 获得存储根目录，优先使用外部存储，其次是内部存储.
     *
     * @param context
     * @return
     */
    public static File getStroageDirectory(Context context) {
        File directory = getExternalStroageDirectory();
        if (directory == null) {
            directory = getInternalStroageDirectory(context);
        }
        return directory;
    }

    /**
     * 获取根目录下的一级目录的文件，没有就创建
     *
     * @param name 根路径下的一级目录的名称
     * @return
     */
    public static File getStroageDirectory(Context context, String name) {
        File rootDir = getStroageDirectory(context);
        if (rootDir == null) {
            return null;
        }

        File directory = new File(rootDir, name);
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return directory;
            } else if (directory.isFile()) {
                directory.delete();
            }
        }
        directory.mkdirs();
        return directory;
    }

    /**
     * 获得外部存储根目录
     *
     * @return
     */
    public static File getExternalStroageDirectory() {
        File directory = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();
        }
        return directory;
    }

    /**
     * 获得应用程序独享的存储根目录
     *
     * @param context
     * @return
     */
    public static File getInternalStroageDirectory(Context context) {
        File directory = null;
        directory = context.getFilesDir();
        return directory;
    }

    /**
     * Dns相关
     *
     * @param c
     * @param file
     * @return
     */
    public static String readAssetFile(Context c, String file) {
        BufferedReader bufReader = null;
        try {
            InputStreamReader inputReader = new InputStreamReader(c.getResources().getAssets().open(file));
            bufReader = new BufferedReader(inputReader);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufReader.readLine()) != null)
                sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            return "";
        } catch (OutOfMemoryError e) {
            return "";
        } finally {
            try {
                bufReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


}

package dotc.android.happybuy.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import dotc.android.happybuy.log.HBLog;

/**
 * Created by huangli on 16/4/1.
 */
public class BitmapUtils {
    public static final String TAG = "BitmapUtils";

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 从uri加载一个bitmap
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 一些photoPath中的图片不是垂直的,通过此函数可以变成垂直的图片
     *
     * @param photoPath
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static Bitmap verticalBitMap(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
        }
        return bitmap;
    }

    /**
     * 旋转图片
     *
     * @param source
     * @param angle
     * @return
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return retVal;
    }

    /**
     * 加载指定宽高的图片来自一个文件（一些bitmap的文件可能非常大,为了节约内存,指定加载指定宽高的图片）
     *
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static Bitmap decodeFromFile(File file, int width, int height) throws FileNotFoundException {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(file), null, options1);
        int scale = 1;
        while (options1.outWidth / scale >= width && options1.outHeight / scale >= height) {
            scale *= 2;
        }

        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = scale;
        return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
    }

    /**
     * 这个函数首先压缩图片，然后再取中间的矩形类容
     *
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }
        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        HBLog.i(TAG + " edgeLength " + edgeLength + " widthOrg " + widthOrg + " heightOrg " + heightOrg);
        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg);
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }
            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;
            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    /**
     * 调整bitmap的大小适应指定的宽高
     *
     * @param context
     * @param bitmapuri
     * @param width
     * @param height
     * @return 压缩后保存图片的uri
     */
    public static Uri compressBitmapFromUri(Context context, Uri bitmapuri, int width, int height) {
        Bitmap bitmapFromUri = getBitmapFromUri(context, bitmapuri);
        if (bitmapFromUri == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFromUri, width, height, true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        String date = sdf.format(new java.util.Date());
        String filename = date + ".jpg";
        File file = new File(context.getCacheDir() + "/" + filename);
        try {
            boolean issuccess = writeBitmapToFile(bitmap, file);
            if (!issuccess) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (bitmapFromUri != null && !bitmapFromUri.isRecycled()) {
            bitmapFromUri.recycle();
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return Uri.fromFile(file);
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
    public static boolean writeBitmapToFile(Bitmap bitmap, File file) throws IOException {
        if (bitmap == null || file == null) {
            return false;
        }
        OutputStream out = null;
        try {
            FileOutputStream stream = new FileOutputStream(file);
            out = new BufferedOutputStream(stream, 1 * 1024 * 1024);
            return bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * bitmap转换成字节数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
        return baos.toByteArray();//创建分配字节数组
    }

    /**
     * 字节数组转换成bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap getBitmap(byte[] data) {
        if (data == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);//从字节数组解码位图
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2 - 5;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2 - 5;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst_left + 15, dst_top + 15, dst_right - 20, dst_bottom - 20);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean saveBitmapToFile(Bitmap b, String uri, int quality, Bitmap.CompressFormat type) {
        try {
            File f = new File(uri);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            b.compress(type, quality, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Bitmap compressFromFile(String path, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, options);
            int realHeight = options.outHeight;
            int realWidth = options.outWidth;
            if (realHeight > maxHeight || realWidth > maxWidth) {
                if (realHeight > realWidth) {
                    options.inSampleSize = realHeight / maxHeight;
                } else {
                    options.inSampleSize = realWidth / maxWidth;
                }
            }
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, options);
        } catch (Exception ignore) {
        }
        return bitmap;
    }

    public static int[] getImageWidthHeightFromFile(String path) {
        int[] widthHeight = new int[2];
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, options);
            widthHeight[0] = options.outWidth;
            widthHeight[1] = options.outHeight;
        } catch (Exception ignore) {
        }
        return widthHeight;
    }
}

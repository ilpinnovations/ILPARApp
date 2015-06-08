package edu.dhbw.andobjviewer;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

public class OCRDataExtractor {
	
	private String pathToImage;
	private String ocrData="";
	private String lang="eng";
	public boolean dataAvailable=false;
	
	public OCRDataExtractor(String path) {
		this.pathToImage = path;
	}
	
	public OCRDataExtractor(String path,String lang) {
		this.pathToImage = path;
		this.lang = lang;
	}
	
	@SuppressLint("SdCardPath")
	public String extractData()
	{
		ExifInterface exif;
		try {
			exif = new ExifInterface(this.pathToImage);
			int exifOrientation = exif.getAttributeInt(
			        ExifInterface.TAG_ORIENTATION,
			        ExifInterface.ORIENTATION_NORMAL);
			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
			    rotate = 90;
			    break;
			case ExifInterface.ORIENTATION_ROTATE_180:
			    rotate = 180;
			    break;
			case ExifInterface.ORIENTATION_ROTATE_270:
			    rotate = 270;
			    break;
			}
			
			BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 4;
		    	
		    Bitmap bitmap = BitmapFactory.decodeFile( this.pathToImage, options );

			if (rotate != 0) {
			    int w = bitmap.getWidth();
			    int h = bitmap.getHeight();

			    // Setting pre rotate
			    Matrix mtx = new Matrix();
			    mtx.preRotate(rotate);

			    // Rotating Bitmap & convert to ARGB_8888, required by tess
			    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			
			TessBaseAPI baseApi = new TessBaseAPI();
			String DATA_PATH = "/sdcard/ILPApp/";
			baseApi.init(DATA_PATH, this.lang);
			baseApi.setImage(bitmap);
			this.ocrData = baseApi.getUTF8Text();
			baseApi.end();
			System.out.println("OCR: "+this.ocrData);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ocrData;
	}
	
    public static boolean isSDCardMounted() {
        boolean isMounted = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isMounted = true;
        } else if (Environment.MEDIA_BAD_REMOVAL.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_CHECKING.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_NOFS.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_REMOVED.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_UNMOUNTABLE.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_UNMOUNTED.equals(state)) {
            isMounted = false;
        }
        return isMounted;
    }

    public static boolean isDirectoryExists(final String filePath) {
        boolean isDirectoryExists = false;
        File mFilePath = new File(filePath);
        if(mFilePath.exists()) {
            isDirectoryExists = true;
        } else {
            isDirectoryExists = mFilePath.mkdirs();
        }
        return isDirectoryExists;
    }

    public static boolean deleteFile(final String filePath) {
        boolean isFileExists = false;
        File mFilePath = new File(filePath);
        if(mFilePath.exists()) {
            mFilePath.delete();
            isFileExists = true;
        }
        return isFileExists;
    }

    public static String getDataPath() {
        String returnedPath = "";
        final String mDirName = "tesseract";
        final String mDataDirName = "tessdata";
        if(isSDCardMounted()) {
            final String mSDCardPath = Environment.getExternalStorageDirectory() + File.separator + mDirName;
            if(isDirectoryExists(mSDCardPath)) {
                final String mSDCardDataPath = Environment.getExternalStorageDirectory() + File.separator + mDirName + 
                        File.separator + mDataDirName;
                isDirectoryExists(mSDCardDataPath);
                return mSDCardPath;
            }
        }
        return returnedPath;
    }

}

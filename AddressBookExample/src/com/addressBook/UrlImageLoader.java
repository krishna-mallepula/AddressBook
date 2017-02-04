package com.addressBook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;

import com.addressBook.util.User;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class UrlImageLoader {
	private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	private File cacheDir;

	// final int stub_id = R.drawable.loding_album;

	private Bitmap mLoadingbmp;

	public UrlImageLoader(Context context) {
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		createExternalDirectory(context);
	}

	private void createExternalDirectory(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment
					.getExternalStorageDirectory(), "walgreens");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			if (!cacheDir.mkdir()) {
			}

	}

	public UrlImageLoader(Context context, boolean isHidden) {
		String WALGREEN_FOLDER = ".walgreen";
		String FILE_PATH_SEPARATOR = "/";
		String directory = "walgreen_cache";
		File dir = null;
		FileOutputStream out = null;
		String mapBundlePath = "";
		mLoadingbmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.loding_album);
		try {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getAbsolutePath();
			mapBundlePath = path + FILE_PATH_SEPARATOR + WALGREEN_FOLDER;
			dir = new File(mapBundlePath);

			if (!dir.exists()) {
				if (!dir.mkdir()) {
					createExternalDirectory(context);
				}
			} else {
				cacheDir = new File(dir.getAbsolutePath(), directory);
				if (!cacheDir.exists()) {
					cacheDir.mkdirs();
				}
			}

		} catch (Exception e) {
			
		}
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
	}

	public void DisplayImage(String url, Activity activity, ImageView imageView) {
		synchronized (cache) {
			try {
				if (cache.containsKey(url)) {
					if (cache.get(url) != null && !cache.get(url).isRecycled())
						imageView.setImageBitmap(cache.get(url));
					else {
						cache.remove(url);
						queuePhoto(url, activity, imageView);
						imageView.setImageBitmap(mLoadingbmp);
					}
				} else {
					queuePhoto(url, activity, imageView);
					imageView.setImageBitmap(mLoadingbmp);
				}
			}

			catch (RuntimeException e) {
				
			} catch (Exception e) {
				
			}
		}
	}

	private final int ERROR=0;
	private final int DEBUG=1;
	private void setLog(String Message,int type)
	{
		switch (type) {
		case ERROR:
			
			break;
		case DEBUG:
			
			break;

		}
		
	}
	
	private void queuePhoto(String url, Activity activity, ImageView imageView) {
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	private Bitmap getBitmap(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		Bitmap b = decodeFile(f);
		if (b != null&& !b.isRecycled())
			return b;

		try {
			Bitmap bitmap = null;
			URL url1 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
			if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
				System.setProperty("http.keepAlive", "false");
			}
			conn.setConnectTimeout(1 * 30 * 1000);
			conn.setReadTimeout(1 * 30 * 1000);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = fetchBitmap(is, f);
			return bitmap;
		} 
		catch (RuntimeException e) {
			
			
		}
		catch (Exception ex) {
			
		}
		return getBitmap(url, f);
	}

	private Bitmap getBitmap(String url, File f) {
		Bitmap bmImg = null;
		try {
			HttpClient client = User.getSSLByPassedHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity buff = new BufferedHttpEntity(entity);
			return fetchBitmap(buff.getContent(), f);
		} catch (MalformedURLException e) {
			Log.e(UrlImageLoader.this.getClass().getSimpleName(),
					"Invalid URL: " + url);

		} catch (IOException e) {
			Log.e(UrlImageLoader.this.getClass().getSimpleName(),
					"Could not load Bitmap from: " + url);
		}
		return bmImg;
	}

	private Bitmap fetchBitmap(InputStream is, File f) {
		Bitmap bitmap = null;
		BufferedInputStream bi = new BufferedInputStream(is);
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			ImageUtils.CopyStream(bi, os);
			bitmap = decodeFile(f);
		} catch (FileNotFoundException e) {
			
		} finally {
			try {
				is.close();
				os.close();
				bi.close();
			} catch (IOException ie) {
				
			}
		}
		return bitmap;
	}

	private Bitmap decodeFile(File f) {
		Bitmap b = null;
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int scale = 1;
			final int REQUIRED_SIZE = 100;
			if (o.outHeight > REQUIRED_SIZE || o.outWidth > REQUIRED_SIZE) {
				scale = (int) Math.pow(2, (int) Math.round(Math
						.log(REQUIRED_SIZE
								/ (double) Math.max(o.outHeight, o.outWidth))
						/ Math.log(0.5)));

			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			BufferedInputStream buf = new BufferedInputStream(
					new FileInputStream(f));
			byte[] bMapArray = new byte[buf.available()];
			buf.read(bMapArray);
			b = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length,
					o2);
			bMapArray = null;
		} 
		
		catch (RuntimeException e) {
			
		}
		catch (Exception e) {
			
		}
		
		return b;
	}

	private class PhotoToLoad {
		public String url;

		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	PhotosQueue photosQueue = new PhotosQueue();

	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		public void Clean(ImageView image) {
			try {
				for (int j = 0; j < photosToLoad.size();) {
					if (photosToLoad.get(j).imageView == image)
						photosToLoad.remove(j);
					else
						++j;
				}

			} catch (Exception e) {
				
			}
		}
	}

	class PhotosLoader extends Thread {
		public void run() {
			try {
				while (true) {
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp = getBitmap(photoToLoad.url);
						if (bmp != null && !bmp.isRecycled()) {
							cache.put(photoToLoad.url, bmp);
							if (((String) photoToLoad.imageView.getTag())
									.equals(photoToLoad.url)) {
								BitmapDisplayer bd = new BitmapDisplayer(bmp,
										photoToLoad.imageView);
								Activity a = (Activity) photoToLoad.imageView
										.getContext();
								a.runOnUiThread(bd);
							}
						}
						bmp = null;
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				
			} catch (RuntimeException e) {
				
			}
			catch (Exception e) {
				
			}
		}
	}

	PhotosLoader photoLoaderThread = new PhotosLoader();

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;

		ImageView imageView;

		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		public void run() {
			try {
				if (bitmap != null && !bitmap.isRecycled())
				{
					imageView.setImageBitmap(bitmap);
					bitmap=null;
				}
				else
					imageView.setImageBitmap(mLoadingbmp);
			}
			catch (RuntimeException e) {

			} catch (Exception e) {
				
			}
		}
	}

	public void clearCache() {
		try {
			if (cache != null) {
				cache.clear();
			}
			File[] files = cacheDir.listFiles();
			for (File f : files)
				f.delete();
		} catch (Exception e) {
			
		}
	}

	public Bitmap getLoadingBmp()
	{
			return mLoadingbmp;
	}
	
	public void recycleBitmap(String url) {
		
		synchronized (cache) {
			try {
				Bitmap tempBmp =null;
				if (cache != null && cache.containsKey(url)) {

					tempBmp = cache.get(url);
					cache.remove(url);
					if (tempBmp != null && !tempBmp.isRecycled()) {
						setLog("Recycling the image", DEBUG);
						tempBmp.recycle();
						tempBmp = null;
					}
					
				}
			} catch (RuntimeException e) {
				
			} catch (Exception e) {
				
			}
		}
		

	}

	public void recycleBitmaps() {
		if (mLoadingbmp != null && !mLoadingbmp.isRecycled()) {
			mLoadingbmp.recycle();
			mLoadingbmp = null;
		}
		if (cache != null) {
			Iterator itr = cache.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry) itr.next();
				if (((Bitmap) e.getValue()) != null
						&& !((Bitmap) e.getValue()).isRecycled()) {

					((Bitmap) e.getValue()).recycle();
				}
			}
		}
	}
}

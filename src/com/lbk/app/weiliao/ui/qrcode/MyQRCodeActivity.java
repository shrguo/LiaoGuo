package com.lbk.app.weiliao.ui.qrcode;

import java.io.UnsupportedEncodingException;

import cn.jpush.android.api.JPushInterface;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lbk.app.weiliao.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MyQRCodeActivity extends Activity {
	// 图片宽度的一般
		private static final int IMAGE_HALFWIDTH = 20;
		// 显示二维码图片
		private ImageView imageview;
		// 插入到二维码里面的图片对象
		private Bitmap mBitmap;
		// 需要插图图片的大小 这里设定为40*40
		int[] pixels = new int[2*IMAGE_HALFWIDTH * 2*IMAGE_HALFWIDTH];
		
		// 用于开发测试时显示本机的imei号
		private TextView tv_myimei;

		@Override
		public void onCreate(Bundle bundle) {
			super.onCreate(bundle);
			
			setContentView(R.layout.activity_myqrcode);
			
			// 构造对象
			imageview = (ImageView) findViewById(R.id.iv_myerweima);
			tv_myimei = (TextView) findViewById(R.id.tv_myimei);
			
//			imageview = new ImageView(this);
	        // 构造需要插入的图片对象
			mBitmap = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.ic_launcher)).getBitmap();
			// 缩放图片
			Matrix m = new Matrix();
			float sx = (float) 2*IMAGE_HALFWIDTH / mBitmap.getWidth();
			float sy = (float) 2*IMAGE_HALFWIDTH / mBitmap.getHeight();
			m.setScale(sx, sy);
			// 重新构造一个40*40的图片
			mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
					mBitmap.getHeight(), m, false);

			try {
				String udid =  JPushInterface.getUdid(getApplicationContext());
				tv_myimei.setText("测试显示："+udid);
//				String s = "仿微信二维码名片";
				imageview.setImageBitmap(cretaeBitmap(new String(udid.getBytes(),
						"ISO-8859-1")));
			} catch (WriterException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
//			setContentView(imageview);
		}

		/**
		 * 生成二维码
		 * 
		 * @param 字符串
		 * @return Bitmap
		 * @throws WriterException
		 */
		public Bitmap cretaeBitmap(String str) throws WriterException {
			// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
			BitMatrix matrix = new MultiFormatWriter().encode(str,
					BarcodeFormat.QR_CODE, 400, 400);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			// 二维矩阵转为一维像素数组,也就是一直横着排了
			int halfW = width / 2;
			int halfH = height / 2;
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH
							&& y < halfH + IMAGE_HALFWIDTH) {
						pixels[y * width + x] = mBitmap.getPixel(x - halfW + IMAGE_HALFWIDTH, y
								- halfH + IMAGE_HALFWIDTH);
					} else {
						if (matrix.get(x, y)) {
							pixels[y * width + x] = 0xff000000;	// 黑色
						}else  {
							pixels[y * width + x] = -1;// -1 相当于0xffffffff 白色
				        }
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

			return bitmap;
		}
}

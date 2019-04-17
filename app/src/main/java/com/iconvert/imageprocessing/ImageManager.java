package com.iconvert.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class ImageManager {

    private final String TAG = "ImageManager";
    private final int SIZE_LEN = 1920;
    private final int SIZE_SHO = 1080;
    private Bitmap mImage;
    private Bitmap mOutputImg;
    private @ColorInt
    int[] pixels;
    private int offset = 0;
    private int stride;
    private int x = 0;
    private int y = 0;
    private int width;
    private int height;
    private Context mContext;

    public ImageManager(Context context) {
        mContext = context;
    }

    public void setImage(Bitmap bitmap) {
        mImage = bitmap;
        mOutputImg = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), mImage.getConfig());
    }

    public void setImage(Uri uri) {
        try {
//            //弃用该减半压缩方式
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
//            mImage = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, options);
            mImage = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
            //压缩
            if (mImage.getHeight() > mImage.getWidth()) {
                if (mImage.getWidth() > SIZE_SHO) {
                    mImage = Bitmap.createScaledBitmap(mImage, SIZE_SHO, SIZE_LEN, true);
                }
            } else {
                if (mImage.getHeight() > SIZE_SHO) {
                    mImage = Bitmap.createScaledBitmap(mImage, SIZE_LEN, SIZE_SHO, true);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (mImage == null) {
                Log.w(TAG, "setImage: image is null");
                return;
            }
            stride = mImage.getWidth();
            width = mImage.getWidth();
            height = mImage.getHeight();
            //mOutputImg = Bitmap.createBitmap(width, height, mImage.getConfig());
            mOutputImg = Bitmap.createScaledBitmap(mImage, width, height, true);
            pixels = new int[width * height];
            mImage.getPixels(pixels, offset, stride, x, y, width, height);
            Log.d(TAG, "Image: width is " + width + " height is " + height);
        }
    }

    public Bitmap getOutputImg() {
        return mOutputImg;
    }

    public void saveImage() {
        ImageProvider.saveImage(mOutputImg);
    }

    //伽马校正 0.5
    public void gamma() {
        @ColorInt int[] tempPixels = new int[pixels.length];
        Log.i(TAG, "gamma");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = Color.red(pixels[x + y * width]);
                int green = Color.green(pixels[x + y * width]);
                int blue = Color.blue(pixels[x + y * width]);
                //0.4 -- gama4 = uint8(img2.^(1/0.4).*256.0);
                tempPixels[x + y * width] = Color.rgb((int) (red * red / 256.0f), (int) (green * green / 256.0f), (int) (blue * blue / 256.0f));
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    //彩图变灰
    public void grayScale() {
        @ColorInt int[] tempPixels = new int[pixels.length];
        Log.i(TAG, "grayScale");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = Color.red(pixels[x + y * width]);
                int green = Color.green(pixels[x + y * width]);
                int blue = Color.blue(pixels[x + y * width]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                tempPixels[x + y * width] = Color.rgb(gray, gray, gray);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    //灰度反转
    public void grayScaleInversion() {
        @ColorInt int[] tempPixels = new int[pixels.length];
        Log.i(TAG, "grayScaleInversion");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = Color.red(pixels[x + y * width]);
                int green = Color.green(pixels[x + y * width]);
                int blue = Color.blue(pixels[x + y * width]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                tempPixels[x + y * width] = Color.rgb(255 - gray, 255 - gray, 255 - gray);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    //均值滤波
    public void avrFiltering() {
        //...
        @ColorInt int[] tempPixels = new int[pixels.length];
        Log.i(TAG, "avrFiltering");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x != 0 && x != width - 1 && y != 0 && y != height - 1) {
                    @ColorInt int red = (Color.red(pixels[x - 1 + (y - 1) * width])
                            + Color.red(pixels[x + (y - 1) * width])
                            + Color.red(pixels[x + 1 + (y - 1) * width])
                            + Color.red(pixels[x - 1 + y * width])
                            + Color.red(pixels[x + y * width])
                            + Color.red(pixels[x + 1 + y * width])
                            + Color.red(pixels[x - 1 + (y + 1) * width])
                            + Color.red(pixels[x + (y + 1) * width])
                            + Color.red(pixels[x + 1 + (y + 1) * width])) / 9;

                    @ColorInt int green = (Color.green(pixels[x - 1 + (y - 1) * width])
                            + Color.green(pixels[x + (y - 1) * width])
                            + Color.green(pixels[x + 1 + (y - 1) * width])
                            + Color.green(pixels[x - 1 + y * width])
                            + Color.green(pixels[x + y * width])
                            + Color.green(pixels[x + 1 + y * width])
                            + Color.green(pixels[x - 1 + (y + 1) * width])
                            + Color.green(pixels[x + (y + 1) * width])
                            + Color.green(pixels[x + 1 + (y + 1) * width])) / 9;

                    @ColorInt int blue = (Color.blue(pixels[x - 1 + (y - 1) * width])
                            + Color.blue(pixels[x + (y - 1) * width])
                            + Color.blue(pixels[x + 1 + (y - 1) * width])
                            + Color.blue(pixels[x - 1 + y * width])
                            + Color.blue(pixels[x + y * width])
                            + Color.blue(pixels[x + 1 + y * width])
                            + Color.blue(pixels[x - 1 + (y + 1) * width])
                            + Color.blue(pixels[x + (y + 1) * width])
                            + Color.blue(pixels[x + 1 + (y + 1) * width])) / 9;

                    tempPixels[x + y * width] = Color.rgb(red, green, blue);

//                    if (x == 500 && y == 500){
//                        Log.i(TAG, "yuan lai: " + pixels[500 + 500 * width] + " jun zhi: " + tempPixels[500 + 500 * width]);
//                        Log.i(TAG, "R yuan lai: " + Color.red(pixels[500 + 500 * width]) + " jun zhi: " + Color.red(tempPixels[500 + 500 * width]));
//                        Log.i(TAG, "G yuan lai: " + Color.green(pixels[500 + 500 * width]) + " jun zhi: " + Color.green(tempPixels[500 + 500 * width]));
//                        Log.i(TAG, "B yuan lai: " + Color.blue(pixels[500 + 500 * width]) + " jun zhi: " + Color.blue(tempPixels[500 + 500 * width]));
//                    }
                } else {
                    tempPixels[y * width + x] = pixels[y * width + x];
                }

            }
        }

        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    //中值滤波
    public void medianFiltering() {
        @ColorInt int[] tempPixels = new int[pixels.length];
        @ColorInt int[] tempArr = new int[9];
        Log.i(TAG, "medianFiltering");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x != 0 && x != width - 1 && y != 0 && y != height - 1) {
                    tempArr[0] = Color.red(pixels[x - 1 + (y - 1) * width]);
                    tempArr[1] = Color.red(pixels[x + (y - 1) * width]);
                    tempArr[2] = Color.red(pixels[x + 1 + (y - 1) * width]);
                    tempArr[3] = Color.red(pixels[x - 1 + y * width]);
                    tempArr[4] = Color.red(pixels[x + y * width]);
                    tempArr[5] = Color.red(pixels[x + 1 + y * width]);
                    tempArr[6] = Color.red(pixels[x - 1 + (y + 1) * width]);
                    tempArr[7] = Color.red(pixels[x + (y + 1) * width]);
                    tempArr[8] = Color.red(pixels[x + 1 + (y + 1) * width]);
                    Arrays.sort(tempArr);
                    @ColorInt int red = tempArr[4];

                    tempArr[0] = Color.green(pixels[x - 1 + (y - 1) * width]);
                    tempArr[1] = Color.green(pixels[x + (y - 1) * width]);
                    tempArr[2] = Color.green(pixels[x + 1 + (y - 1) * width]);
                    tempArr[3] = Color.green(pixels[x - 1 + y * width]);
                    tempArr[4] = Color.green(pixels[x + y * width]);
                    tempArr[5] = Color.green(pixels[x + 1 + y * width]);
                    tempArr[6] = Color.green(pixels[x - 1 + (y + 1) * width]);
                    tempArr[7] = Color.green(pixels[x + (y + 1) * width]);
                    tempArr[8] = Color.green(pixels[x + 1 + (y + 1) * width]);
                    Arrays.sort(tempArr);
                    @ColorInt int green = tempArr[4];

                    tempArr[0] = Color.blue(pixels[x - 1 + (y - 1) * width]);
                    tempArr[1] = Color.blue(pixels[x + (y - 1) * width]);
                    tempArr[2] = Color.blue(pixels[x + 1 + (y - 1) * width]);
                    tempArr[3] = Color.blue(pixels[x - 1 + y * width]);
                    tempArr[4] = Color.blue(pixels[x + y * width]);
                    tempArr[5] = Color.blue(pixels[x + 1 + y * width]);
                    tempArr[6] = Color.blue(pixels[x - 1 + (y + 1) * width]);
                    tempArr[7] = Color.blue(pixels[x + (y + 1) * width]);
                    tempArr[8] = Color.blue(pixels[x + 1 + (y + 1) * width]);
                    Arrays.sort(tempArr);
                    @ColorInt int blue = tempArr[4];

                    tempPixels[x + y * width] = Color.rgb(red, green, blue);

                } else {
                    tempPixels[y * width + x] = pixels[y * width + x];
                }

            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    //原图
    public void setSrcPic() {
        mOutputImg = mImage;
    }

    //直方图均衡化
    public void histogramFiltering() {
        int[] tempPixels = new int[width * height];
        int[][] rgbhis = new int[3][256]; // RGB
        int[][] newrgbhis = new int[3][256]; // after HE
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 256; j++) {
                rgbhis[i][j] = 0;
                newrgbhis[i][j] = 0;
            }
        }
        int index = 0;
        int totalPixelNumber = height * width;
        for (int row = 0; row < height; row++) {
            int tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                tr = Color.red(pixels[index]);
                tg = Color.green(pixels[index]);
                tb = Color.blue(pixels[index]);

                // generate original source image RGB histogram
                rgbhis[0][tr]++;
                rgbhis[1][tg]++;
                rgbhis[2][tb]++;
            }
        }
        // generate original source image RGB histogram
        generateHEData(newrgbhis, rgbhis, totalPixelNumber, 256);
        for (int row = 0; row < height; row++) {
            int tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                tr = Color.red(pixels[index]);
                tg = Color.green(pixels[index]);
                tb = Color.blue(pixels[index]);

                // get output pixel now...
                tr = newrgbhis[0][tr];
                tg = newrgbhis[1][tg];
                tb = newrgbhis[2][tb];
                tempPixels[index] = Color.rgb(tr, tg, tb);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    private void generateHEData(int[][] newrgbhis, int[][] rgbhis, int totalPixelNumber, int grayLevel) {
        for (int i = 0; i < grayLevel; i++) {
            newrgbhis[0][i] = getNewIntensityRate(rgbhis[0], totalPixelNumber, i);
            newrgbhis[1][i] = getNewIntensityRate(rgbhis[1], totalPixelNumber, i);
            newrgbhis[2][i] = getNewIntensityRate(rgbhis[2], totalPixelNumber, i);
        }
    }

    private int getNewIntensityRate(int[] grayHis, double totalPixelNumber, int index) {
        double sum = 0;
        for (int i = 0; i <= index; i++) {
            sum += ((double) grayHis[i]) / totalPixelNumber;
        }
        return (int) (sum * 255.0);
    }

    //彩色负片
    public void negativeFilm() {
        @ColorInt int[] tempPixels = new int[pixels.length];
        Log.i(TAG, "negativeFilm");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tempPixels[x + y * width] = 0xFFFFFFFF - pixels[x + y * width] + 0xFF000000;
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    //拉普拉斯锐化
    public void laplaceProcess() {
        Log.i(TAG, "laplaceProcess");
        // 拉普拉斯算子
        int[] LAPLACE = new int[]{0, -1, 0, -1, 4, -1, 0, -1, 0};
        int[] tempPixels = new int[width * height];

        int k0 = 0, k1 = 0, k2 = 0;
        int k3 = 0, k4 = 0, k5 = 0;
        int k6 = 0, k7 = 0, k8 = 0;

        k0 = LAPLACE[0];
        k1 = LAPLACE[1];
        k2 = LAPLACE[2];
        k3 = LAPLACE[3];
        k4 = LAPLACE[4];
        k5 = LAPLACE[5];
        k6 = LAPLACE[6];
        k7 = LAPLACE[7];
        k8 = LAPLACE[8];
        int offs = 0;

        int r = 0, g = 0, b = 0;
        for (int row = 1; row < height - 1; row++) {
            offs = row * width;
            for (int col = 1; col < width - 1; col++) {
                // red
                r = k0 * Color.red(pixels[offs - width + col - 1])
                        + k1 * Color.red(pixels[offs - width + col])
                        + k2 * Color.red(pixels[offs - width + col + 1])
                        + k3 * Color.red(pixels[offs + col - 1])
                        + k4 * Color.red(pixels[offs + col])
                        + k5 * Color.red(pixels[offs + col + 1])
                        + k6 * Color.red(pixels[offs + width + col - 1])
                        + k7 * Color.red(pixels[offs + width + col])
                        + k8 * Color.red(pixels[offs + width + col + 1]);
                // green
                g = k0 * Color.green(pixels[offs - width + col - 1]) +
                        k1 * Color.green(pixels[offs - width + col]) +
                        k2 * Color.green(pixels[offs - width + col + 1]) +
                        k3 * Color.green(pixels[offs + col - 1]) +
                        k4 * Color.green(pixels[offs + col]) +
                        k5 * Color.green(pixels[offs + col + 1]) +
                        k6 * Color.green(pixels[offs + width + col - 1]) +
                        k7 * Color.green(pixels[offs + width + col]) +
                        k8 * Color.green(pixels[offs + width + col + 1]);
                // blue
                b = k0 * Color.blue(pixels[offs - width + col - 1]) +
                        k1 * Color.blue(pixels[offs - width + col]) +
                        k2 * Color.blue(pixels[offs - width + col + 1]) +
                        k3 * Color.blue(pixels[offs + col - 1]) +
                        k4 * Color.blue(pixels[offs + col]) +
                        k5 * Color.blue(pixels[offs + col + 1]) +
                        k6 * Color.blue(pixels[offs + width + col - 1]) +
                        k7 * Color.blue(pixels[offs + width + col]) +
                        k8 * Color.blue(pixels[offs + width + col + 1]);

                tempPixels[offs + col] = Color.rgb(r, g, b);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    //显示傅立叶变换频谱
    public void fft() {
        // 赋初值
        int w = 1;
        int h = 1;
        // 计算进行付立叶变换的宽度和高度（2的整数次方）
        while (w * 2 <= width) {
            w *= 2;
        }
        while (h * 2 <= height) {
            h *= 2;
        }
        // 分配内存
        Complex[] src = new Complex[h * w];
        Complex[] dest = new Complex[h * w];
        int[] newPixels = new int[h * w];
        // 初始化newPixels
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                newPixels[i * w + j] = gray;
            }
        }
        // 初始化src,dest
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                dest[i * w + j] = new Complex();
                src[i * w + j] = new Complex(newPixels[i * w + j], 0);
            }
        }
        // 在y方向上进行快速傅立叶变换
        for (int i = 0; i < h; i++) {
            Fft.fft(src, i, w, dest);
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                src[j * h + i] = dest[i * w + j];
            }
        }
        // 对x方向进行傅立叶变换
        for (int i = 0; i < w; i++) {
            Fft.fft(src, i, h, dest);
        }
        /**
         * 将图像看做二维函数，图像灰度值为函数在相应XY处的函数值，对其进行二维快速傅里叶变换，
         * 得到一个复数矩阵，将此矩阵水平循环移动半宽，垂直循环移动半高。
         */
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double re = dest[j * h + i].re;
                double im = dest[j * h + i].im;
                int ii = 0, jj = 0;
                int temp = (int) (Math.sqrt(re * re + im * im) / 100);
                if (temp > 255) {
                    temp = 255;
                }
                if (i < h / 2) {
                    ii = i + h / 2;
                } else {
                    ii = i - h / 2;
                }
                if (j < w / 2) {
                    jj = j + w / 2;
                } else {
                    jj = j - w / 2;
                }
                newPixels[ii * w + jj] = temp;
            }
        }
        int[] tempPixels = new int[height * width];
        Log.i(TAG, "fft: " + w + " " + h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int gray = newPixels[i * w + j];
                tempPixels[i * width + j] = Color.rgb(gray, gray, gray);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);

    }

    //频率域低通 高通滤波；
    //实现频率域逆滤波图像复原和维纳滤波图像复原；
    public void lowPassFiltering() {
        int w = 1;
        int h = 1;
        // 计算进行付立叶变换的宽度和高度（2的整数次方）
        while (w * 2 <= width) {
            w *= 2;
        }
        while (h * 2 <= height) {
            h *= 2;
        }
        double k = 0.0001;
        double[] H = new double[h * w];
        for (int row = 1; row < w; row++)
            for (int column = 1; column < h; column++) {
                H[row * w + column] = Math.exp(-k * (Math.pow(row - w / 2.0 , 2) + Math.pow(Math.pow((column - h / 2) , 2) , (5.0 / 6))));
            }

        // 分配内存
        Complex[] src = new Complex[h * w];
        Complex[] dest = new Complex[h * w];
        int[] newPixels = new int[h * w];
        // 初始化newPixels
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                newPixels[i * w + j] = gray;
            }
        }
        // 初始化src,dest
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                dest[i * w + j] = new Complex();
                src[i * w + j] = new Complex(newPixels[i * w + j], 0);
            }
        }
        // 在y方向上进行快速傅立叶变换
        for (int i = 0; i < h; i++) {
            Fft.fft(src, i, w, dest);
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                src[j * h + i] = dest[i * w + j];
            }
        }
        // 对x方向进行傅立叶变换
        for (int i = 0; i < w; i++) {
            Fft.fft(src, i, h, dest);
        }
        /**
         * 将图像看做二维函数，图像灰度值为函数在相应XY处的函数值，对其进行二维快速傅里叶变换，
         * 得到一个复数矩阵，将此矩阵水平循环移动半宽，垂直循环移动半高。
         */
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double re = dest[j * h + i].re;
                double im = dest[j * h + i].im;
                int ii = 0, jj = 0;
                int temp = (int) (Math.sqrt(re * re + im * im) / 100);
                if (temp > 255) {
                    temp = 255;
                }
                if (i < h / 2) {
                    ii = i + h / 2;
                } else {
                    ii = i - h / 2;
                }
                if (j < w / 2) {
                    jj = j + w / 2;
                } else {
                    jj = j - w / 2;
                }
                newPixels[ii * w + jj] = temp;
            }
        }
        int[] tempPixels = new int[height * width];
        Log.i(TAG, "fft: " + w + " " + h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                tempPixels[i * width + j] = Color.rgb(gray, gray, gray);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    public void highPassFiltering() {
        int w = 1;
        int h = 1;
        // 计算进行付立叶变换的宽度和高度（2的整数次方）
        while (w * 2 <= width) {
            w *= 2;
        }
        while (h * 2 <= height) {
            h *= 2;
        }
        double k = 0.0001;
        double[] H = new double[h * w];
        for (int row = 1; row < w; row++)
            for (int column = 1; column < h; column++) {
                H[row * w + column] = Math.exp(-k * (Math.pow(row - w / 2.0 , 2) + Math.pow(Math.pow((column - h / 2) , 2) , (5.0 / 6))));
            }

        // 分配内存
        Complex[] src = new Complex[h * w];
        Complex[] dest = new Complex[h * w];
        int[] newPixels = new int[h * w];
        // 初始化newPixels
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                newPixels[i * w + j] = gray;
            }
        }
        // 初始化src,dest
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                dest[i * w + j] = new Complex();
                src[i * w + j] = new Complex(newPixels[i * w + j], 0);
            }
        }
        // 在y方向上进行快速傅立叶变换
        for (int i = 0; i < h; i++) {
            Fft.fft(src, i, w, dest);
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                src[j * h + i] = dest[i * w + j];
            }
        }
        // 对x方向进行傅立叶变换
        for (int i = 0; i < w; i++) {
            Fft.fft(src, i, h, dest);
        }
        /**
         * 将图像看做二维函数，图像灰度值为函数在相应XY处的函数值，对其进行二维快速傅里叶变换，
         * 得到一个复数矩阵，将此矩阵水平循环移动半宽，垂直循环移动半高。
         */
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double re = dest[j * h + i].re;
                double im = dest[j * h + i].im;
                int ii = 0, jj = 0;
                int temp = (int) (Math.sqrt(re * re + im * im) / 100);
                if (temp > 255) {
                    temp = 255;
                }
                if (i < h / 2) {
                    ii = i + h / 2;
                } else {
                    ii = i - h / 2;
                }
                if (j < w / 2) {
                    jj = j + w / 2;
                } else {
                    jj = j - w / 2;
                }
                newPixels[ii * w + jj] = temp;
            }
        }
        int[] tempPixels = new int[height * width];
        Log.i(TAG, "fft: " + w + " " + h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                tempPixels[i * width + j] = Color.rgb(0x00, 0x00, 0x00);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    public void inverseFiltering() {
        // 赋初值
        int w = 1;
        int h = 1;
        // 计算进行付立叶变换的宽度和高度（2的整数次方）
        while (w * 2 <= width) {
            w *= 2;
        }
        while (h * 2 <= height) {
            h *= 2;
        }
        double k = 0.0001;
        double[] H = new double[h * w];
        for (int row = 1; row < w; row++)
            for (int column = 1; column < h; column++) {
                H[row * w + column] = Math.exp(-k * (Math.pow(row - w / 2.0 , 2) + Math.pow(Math.pow((column - h / 2) , 2) , (5.0 / 6))));
            }

        // 分配内存
        Complex[] src = new Complex[h * w];
        Complex[] dest = new Complex[h * w];
        int[] newPixels = new int[h * w];
        // 初始化newPixels
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                newPixels[i * w + j] = gray;
            }
        }
        // 初始化src,dest
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                dest[i * w + j] = new Complex();
                src[i * w + j] = new Complex(newPixels[i * w + j], 0);
            }
        }
        // 在y方向上进行快速傅立叶变换
        for (int i = 0; i < h; i++) {
            Fft.fft(src, i, w, dest);
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                src[j * h + i] = dest[i * w + j];
            }
        }
        // 对x方向进行傅立叶变换
        for (int i = 0; i < w; i++) {
            Fft.fft(src, i, h, dest);
        }
        /**
         * 将图像看做二维函数，图像灰度值为函数在相应XY处的函数值，对其进行二维快速傅里叶变换，
         * 得到一个复数矩阵，将此矩阵水平循环移动半宽，垂直循环移动半高。
         */
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double re = dest[j * h + i].re;
                double im = dest[j * h + i].im;
                int ii = 0, jj = 0;
                int temp = (int) (Math.sqrt(re * re + im * im) / 100);
                if (temp > 255) {
                    temp = 255;
                }
                if (i < h / 2) {
                    ii = i + h / 2;
                } else {
                    ii = i - h / 2;
                }
                if (j < w / 2) {
                    jj = j + w / 2;
                } else {
                    jj = j - w / 2;
                }
                newPixels[ii * w + jj] = temp;
            }
        }
        int[] tempPixels = new int[height * width];
        Log.i(TAG, "fft: " + w + " " + h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                tempPixels[i * width + j] = Color.rgb(gray, gray, gray);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }

    public void wienerFiltering() {
        int w = 1;
        int h = 1;
        // 计算进行付立叶变换的宽度和高度（2的整数次方）
        while (w * 2 <= width) {
            w *= 2;
        }
        while (h * 2 <= height) {
            h *= 2;
        }
        double k = 0.0001;
        double[] H = new double[h * w];
        for (int row = 1; row < w; row++)
            for (int column = 1; column < h; column++) {
                H[row * w + column] = Math.exp(-k * (Math.pow(row - w / 2.0 , 2) + Math.pow(Math.pow((column - h / 2) , 2) , (5.0 / 6))));
            }

        // 分配内存
        Complex[] src = new Complex[h * w];
        Complex[] dest = new Complex[h * w];
        int[] newPixels = new int[h * w];
        // 初始化newPixels
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                newPixels[i * w + j] = gray;
            }
        }
        // 初始化src,dest
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                dest[i * w + j] = new Complex();
                src[i * w + j] = new Complex(newPixels[i * w + j], 0);
            }
        }
        // 在y方向上进行快速傅立叶变换
        for (int i = 0; i < h; i++) {
            Fft.fft(src, i, w, dest);
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                src[j * h + i] = dest[i * w + j];
            }
        }
        // 对x方向进行傅立叶变换
        for (int i = 0; i < w; i++) {
            Fft.fft(src, i, h, dest);
        }
        /**
         * 将图像看做二维函数，图像灰度值为函数在相应XY处的函数值，对其进行二维快速傅里叶变换，
         * 得到一个复数矩阵，将此矩阵水平循环移动半宽，垂直循环移动半高。
         */
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double re = dest[j * h + i].re;
                double im = dest[j * h + i].im;
                int ii = 0, jj = 0;
                int temp = (int) (Math.sqrt(re * re + im * im) / 100);
                if (temp > 255) {
                    temp = 255;
                }
                if (i < h / 2) {
                    ii = i + h / 2;
                } else {
                    ii = i - h / 2;
                }
                if (j < w / 2) {
                    jj = j + w / 2;
                } else {
                    jj = j - w / 2;
                }
                newPixels[ii * w + jj] = temp;
            }
        }
        int[] tempPixels = new int[height * width];
        Log.i(TAG, "fft: " + w + " " + h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int red = Color.red(pixels[i * width + j]);
                int green = Color.green(pixels[i * width + j]);
                int blue = Color.blue(pixels[i * width + j]);
                int gray = (red * 76 + green * 150 + blue * 30) >> 8;
                tempPixels[i * width + j] = Color.rgb(gray, gray, gray);
            }
        }
        mOutputImg.setPixels(tempPixels, offset, stride, x, y, width, height);
    }
}

package com.iconvert.imageprocessing;

public class Fft {

    // compute the FFT of x[], assuming its length is a power of 2
    public static void fft(Complex[] src, int row, int width, Complex[] dest) {

        Complex[] temp = new Complex[width];
        for (int k = 0; k < width; k++) {
            temp[k] = src[row * width + k];
        }
        temp = fft(temp);
        //set output
        for (int k = 0; k < width; k++) {
            dest[row * width + k] = temp[k];
        }
    }

    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1)
            return new Complex[]{x[0]};

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {
            throw new RuntimeException("N is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            even[k] = x[2 * k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd = even; // reuse the array
        for (int k = 0; k < N / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + N / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }

        return y;

    }

//    private int getWeight(int k, int l, int r) {
//        int d = r-l;	//位移量
//        k = k>>d;
//        return reverseRatio(k, r);
//    }
//    private int reverseRatio(int k, int r) {
//        int n = 0;
//        StringBuilder sb = new StringBuilder(Integer.toBinaryString(k));
//        StringBuilder sb2 = new StringBuilder("");
//        if(sb.length()<r) {
//            n = r-sb.length();
//            for(int i=0; i<n; i++) {
//                sb.insert(0, "0");
//            }
//        }
//
//        for(int i=0; i<sb.length(); i++) {
//            sb2.append(sb.charAt(sb.length()-i-1));
//        }
//        return Integer.parseInt(sb2.toString(), 2);
//    }
//
//    public Complex[] fft(Complex[] values) {
//        int n = values.length;
//        int r = (int)(Math.log10(n)/Math.log10(2));	//求迭代次数r
//        Complex[][] temp = new Complex[r+1][n];	//计算过程的临时矩阵
//        Complex w = new Complex(); 	//权系数
//        temp[0] = values;
//        int x1, x2;	//一对对偶结点的下标值
//        int p, t;	//p表示加权系数Wpn的p值, t是重新排序后对应的序数值
//        for(int l=1; l<=r; l++) {
//            if(l != r) {
//                for(int k=0; k<n; k++) {
//                    if(k < n/Math.pow(2, l)) {
//                        x1 = k;
//                        x2 = x1 + (int)(n/Math.pow(2, l));
//                    } else {
//                        x2 = k;
//                        x1 = x2 - (int)(n/Math.pow(2, l));
//                    }
//                    p = getWeight(k, l, r);
//                    //xi(j) = temp[i-1][x1] + Wpn* temp[i-1][x2];
//                    w.setA(Math.cos(-2*Math.PI*p/n));
//                    w.setB(Math.sin(-2*Math.PI*p/n));
//                    temp[l][k] = Complex.add(temp[l-1][x1] , Complex.multiply(w, temp[l-1][x2]) );
//
//                }
//            } else {
//                for(int k=0; k<n/2; k++) {
//                    x1 = 2*k;
//                    x2 = 2*k+1;
//                    //System.out.println("x1:" + x1 + "  x2:" + x2);
//                    t = reverseRatio(2*k, r);
//                    p = t;
//                    w.setA(Math.cos(-2*Math.PI*p/n));
//                    w.setB(Math.sin(-2*Math.PI*p/n));
//                    temp[l][t] = Complex.add(temp[l-1][x1] , Complex.multiply(w, temp[l-1][x2]) );
//                    t = reverseRatio(2*k+1, r);
//                    p = t;
//                    w.setA(Math.cos(-2*Math.PI*p/n));
//                    w.setB(Math.sin(-2*Math.PI*p/n));
//                    temp[l][t] = Complex.add(temp[l-1][x1] , Complex.multiply(w, temp[l-1][x2]) );
//                }
//            }
//        }
//        return temp[r];
//    }
//    public Complex[][] fft(Complex matrix[][], int w, int h) {
//        double r1 = Math.log10(w)/Math.log10(2.0) - (int)(Math.log10(w)/Math.log10(2.0));
//        double r2 = Math.log10(h)/Math.log10(2.0) - (int)(Math.log10(w)/Math.log10(2.0));
//        if(r1 != 0.0 || r2 != 0.0) {
//            System.err.println("输入的参数w或h不是2的n次幂！");
//            return null;
//        }
//        int r = 0;
//        r = (int)(Math.log10(w)/Math.log10(2));
//        //进行行傅里叶变换
//        for(int i=0; i<h; i++) {
//            matrix[i] = fft(matrix[i]);
//        }
//        //进行列傅里叶变换
//        int n = h;
//        r = (int)(Math.log10(n)/Math.log10(2));	//求迭代次数r
//        Complex tempCom[] = new Complex[h];
//        for(int j=0; j<w; j++) {
//            for(int i=0; i<h; i++) {
//                tempCom[i] = matrix[i][j];
//            }
//            tempCom = fft(tempCom);
//            for(int i=0; i<h; i++) {
//                matrix[i][j] = tempCom[i];
//            }
//        }
//        return matrix;
//    }

}

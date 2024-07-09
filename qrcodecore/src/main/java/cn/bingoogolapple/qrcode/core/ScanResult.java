package cn.bingoogolapple.qrcode.core;

import android.graphics.PointF;

import com.google.zxing.BarcodeFormat;

/**
 * 作者:王浩
 * 创建时间:2018/6/15
 * 描述:
 */
public class ScanResult {
    public String result;
    public BarcodeFormat format;
    PointF[] resultPoints;

    public ScanResult(String result,BarcodeFormat format) {
        this.result = result;
        this.format = format;
    }

    public ScanResult(String result, PointF[] resultPoints) {
        this.result = result;
        this.resultPoints = resultPoints;
    }
}

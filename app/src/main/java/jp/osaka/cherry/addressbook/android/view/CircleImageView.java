package jp.osaka.cherry.addressbook.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 円画像
 */
public class CircleImageView extends AppCompatImageView {

    /**
     * @serial キャンバスサイズ
     */
    private int canvasSize;

    /**
     * @serial ペイント
     */
    private Paint paint;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public CircleImageView(Context context) {
        super(context, null);
    }

    /**
     * 円画像
     *
     * @param context コンテキスト
     * @param attrs 属性
     */
    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs 属性
     * @param defStyle スタイル
     */
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            BitmapDrawable drawable = (BitmapDrawable) getDrawable();

            if (drawable == null) return;
            if (getWidth() == 0 || getHeight() == 0) return;

            Bitmap srcBmp = drawable.getBitmap();
            if (srcBmp == null) return;


            Bitmap image = getSquareBitmap(srcBmp);

            canvasSize = getWidth();
            if (getHeight() < canvasSize)
                canvasSize = getHeight();

            @SuppressLint("DrawAllocation") BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);

            int circleCenter = canvasSize / 2;
            canvas.drawCircle(circleCenter, circleCenter, circleCenter - 1, paint);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
    }

    /**
     * 四角画像取得
     *
     * @param srcBmp 画像
     * @return 画像
     */
    private Bitmap getSquareBitmap(Bitmap srcBmp) {
        if (srcBmp.getWidth() == srcBmp.getHeight()) return srcBmp;

        //Rectangle to square. Equivarent to ScaleType.CENTER_CROP
        int dim = Math.min(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        float w = (dim - srcBmp.getWidth()) >> 1;
        float h = (dim - srcBmp.getHeight()) >> 1;
        float left = srcBmp.getWidth() > dim ? w : 0;
        float top = srcBmp.getHeight() > dim ? h : 0;
        canvas.drawBitmap(srcBmp, left, top, null);

        return dstBmp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 幅計測
     *
     * @param measureSpec スペック
     * @return 幅
     */
    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize;
        }

        return result;
    }

    /**
     * 高さ計測
     *
     * @param measureSpecHeight スペック
     * @return 高さ
     */
    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize;
        }

        return (result + 2);
    }
}

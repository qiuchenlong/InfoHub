package com.a10.infohub.ui.seattable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.a10.infohub.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;
import static android.R.attr.scaleX;
import static android.R.attr.scaleY;


/**
 * Created by qiuchenlong on 2017/6/30.
 */

public class SeatTable extends View {


    private final boolean DBG = false;

    Paint paint = new Paint();

    Matrix matrix = new Matrix();

    Matrix tempMatrix = new Matrix();


    /**
     * 座位水平间距
     */
    int spacing;

    /**
     * 座位垂直间距
     */
    int verSpacing;


    /**
     * 行号宽度
     */
    int numberWidth;


    /**
     * 行数
     */
    int row;


    /**
     * 列数
     */
    int column;

    /**
     * 可选时座位的图片
     */
    Bitmap seatBitmap;

    /**
     * 选中时座位的图片
     */
    Bitmap checkedSeatBitmap;


    int lastX;
    int lastY;

    /**
     * 整个座位图的宽度
     */
    int seatBitmapWidth;

    /**
     * 整个座位图的高度
     */
    int seatBitmapHeight;

    /**
     * 屏幕高度
     */
    float screenHeight;


    /**
     * 荧幕默认宽度与座位图的比例
     */
    float screenWidthScale = 0.5f;


    /**
     * 屏幕最小的宽度
     */
    int defaultScreenWidth;


    /**
     * 标识是否正在缩放
     */
    boolean isScaling;
    float scaleX, scaleY;


    /**
     * 是否是第一次缩放
     */
    boolean firstScale = true;

    /**
     * 最多可以选择的座位数量
     */
    int maxSelected = Integer.MAX_VALUE;

    private SeatChecker seatChecker;

    /**
     * 荧幕名称
     */
    private String screenName = "速度与激情8";


    boolean isOnClick;

    /**
     * 座位已售
     */
    private static final int SEAT_TYPE_SOLD = 1;

    /**
     * 座位已经选中
     */
    private static final int SEAT_TYPE_SELECTED = 2;

    /**
     * 座位可选
     */
    private static final int SEAT_TYPE_AVAILABLE = 3;

    /**
     * 座位不可用
     */
    private static final int SEAT_TYPE_NOT_AVAILABLE = 4;

    private int downX, downY;
    private boolean pointer;

    public ArrayList<Point> list;



    /**
     * 顶部高度,可选,已选,已售区域的高度
     */
    float headHeight;

    Paint pathPaint;
    RectF rectF;


    /**
     * 头部下面横线的高度
     */
    int borderHeight = 1;
    Paint redBorderPaint;


    /**
     * 默认的座位图宽度，如果使用的自己的座位图片比这个尺寸大或者小，会缩放到这个大小
     */
    private float defaultImgW = 40;

    /**
     * 默认的座位图高度
     */
    private float defaultImgH = 34;

    /**
     * 座位图片的宽度
     */
    private int seatWidth = 40;

    /**
     * 座位图片的高度
     */
    private int seatHeight = 34;

    private float zoom;

    float xScale1 = 1;

    float yScale1 = 1;


    public SeatTable(Context context) {
        super(context);
    }

    public SeatTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeatTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public SeatTable(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }


    public void setData(int row, int column){
        this.row = row;
        this.column = column;
        init();
        invalidate();
    }

    public void init() {
        spacing = (int) dip2Px(5);
        verSpacing = (int) dip2Px(10);
        defaultScreenWidth = (int) dip2Px(80);
        seatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trip_train_bg_seat_unselected);
        xScale1 = defaultImgW / seatBitmap.getWidth();
        yScale1 = defaultImgH / seatBitmap.getHeight();
        checkedSeatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trip_train_bg_seat_selected);
        seatBitmapWidth = (int) (column * seatBitmap.getWidth() * xScale1 + (column - 1) * spacing);
        seatBitmapHeight = (int) (row * seatBitmap.getHeight() * yScale1 + (row - 1) * verSpacing);
        paint.setColor(Color.RED);
        numberWidth = (int) dip2Px(20);
        screenHeight = dip2Px(20);

        headHeight = dip2Px(30);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));

        list = new ArrayList<>();
        matrix.postTranslate(numberWidth + spacing, screenHeight + borderHeight + verSpacing);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (row <= 0 || column <= 0) {
            return;
        }
        drawSeat(canvas);

        drawScreen(canvas);

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            pointer = true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointer = false;
                downX = x;
                downY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                autoScale();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && !isOnClick) {
                    int downDX = Math.abs(x - downX);
                    int downDY = Math.abs(y - downY);
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        int dx = x - lastX;
                        int dy = y - lastY;
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }
                }
                break;
        }

        lastX = x;
        lastY = y;
        isOnClick = false;

        return true;
    }

    private void drawSeat(Canvas canvas) {
        zoom = getMatrixScaleX();
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        float scaleX = zoom;
        float scaleY = zoom;
        for (int i = 0; i < row; i++) {
            float top = i * seatBitmap.getHeight() * yScale1 * scaleY + i * verSpacing * scaleY + translateY;
            float bottom = top + seatBitmap.getHeight() * yScale1 * scaleY;

            if (bottom < 0 || top > getHeight()) {
                continue;
            }

            for (int j = 0; j < column; j++) {
                float left = j * seatBitmap.getWidth() * xScale1 * scaleX + j * spacing * scaleX +translateX;
                float right = left + seatBitmap.getWidth() * xScale1 * scaleY;

                if (right < 0 || left > getWidth()) {
                    continue;
                }


                int seatType = getSeatType(i, j);
                tempMatrix.setTranslate(left, top);
                tempMatrix.postScale(xScale1, yScale1, left, top);
                tempMatrix.postScale(scaleX, scaleY, left, top);

//                if (isHave(i, j)) {
//                    canvas.drawBitmap(checkedSeatBitmap, tempMatrix, paint);
//                    drawText(canvas, i, j, top, left);
//                } else {
//                    canvas.drawBitmap(seatBitmap, tempMatrix, paint);
//                }

                switch (seatType) {
                    case SEAT_TYPE_AVAILABLE:
                        canvas.drawBitmap(seatBitmap, tempMatrix, paint);
                        break;
                    case SEAT_TYPE_NOT_AVAILABLE:
                        break;
                    case SEAT_TYPE_SELECTED:
                        canvas.drawBitmap(checkedSeatBitmap, tempMatrix, paint);
                        drawText(canvas, i, j, top, left);
                        break;
                    case SEAT_TYPE_SOLD:
//                        canvas.drawBitmap(seatSoldBitmap, tempMatrix, paint);
                        break;
                }
            }
        }
    }

    /**
     * 绘制中间屏幕
     */
    void drawScreen(Canvas canvas) {
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));
        float startY = headHeight + borderHeight;

        float centerX = seatBitmapWidth * getMatrixScaleX() / 2 + getTranslateX();
        float screenWidth = seatBitmapWidth * screenWidthScale * getMatrixScaleX();
        if (screenWidth < defaultScreenWidth) {
            screenWidth = defaultScreenWidth;
        }

        Path path = new Path();
        path.moveTo(centerX, startY);
        path.lineTo(centerX - screenWidth / 2, startY);
        path.lineTo(centerX - screenWidth / 2 + 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2 - 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2, startY);

        canvas.drawPath(path, pathPaint);

        pathPaint.setColor(Color.BLACK);
        pathPaint.setTextSize(20 * getMatrixScaleX());

        canvas.drawText(screenName, centerX - pathPaint.measureText(screenName) / 2, getBaseLine(pathPaint, startY, startY + screenHeight * getMatrixScaleY()), pathPaint);
    }


    private int getSeatType(int row, int column) {

        if (isHave(getID(row, column)) >= 0) {
            return SEAT_TYPE_SELECTED;
        }

        if (seatChecker != null) {
            if (!seatChecker.isValidSeat(row, column)) {
                return SEAT_TYPE_NOT_AVAILABLE;
            } else if (seatChecker.isSold(row, column)) {
                return SEAT_TYPE_SOLD;
            }
        }

        return SEAT_TYPE_AVAILABLE;
    }

    private int getID(int row, int column) {
        return row * this.column + (column + 1);
    }




    private void autoScale() {
        if (getMatrixScaleX() > 2.2) {
            zoomAnimate(getMatrixScaleX(), 2.0f);
        } else if (getMatrixScaleX() < 0.98) {
            zoomAnimate(getMatrixScaleX(), 1.0f);
        }
    }

    private void zoomAnimate(float cur, float tar) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cur, tar);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        ZoomAnimation zoomAnim = new ZoomAnimation();
        valueAnimator.addUpdateListener(zoomAnim);
        valueAnimator.addListener(zoomAnim);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }


    private void zoom(float zoom) {
        float z = zoom / getMatrixScaleX();
        matrix.postScale(z, z, scaleX, scaleY);
        invalidate();
    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {


        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            zoom = (Float) animation.getAnimatedValue();
            zoom(zoom);

            if (DBG) {
                Log.d("zoomTest", "zoom:" + zoom);
                Log.d("zoomTest", "zoom:" + zoom);
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
    ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isScaling = true;
            float scaleFactor = detector.getScaleFactor();
            if (getMatrixScaleY() * scaleFactor > 3) {
                scaleFactor = 3 / getMatrixScaleY();
            }
            if (firstScale) {
                scaleX = detector.getCurrentSpanX();
                scaleY = detector.getCurrentSpanY();
                firstScale = false;
            }

            if (getMatrixScaleY() * scaleFactor < 0.5) {
                scaleFactor = (float) (0.5 / getMatrixScaleY());
            }
            matrix.postScale(scaleFactor, scaleFactor, scaleX, scaleY);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
            firstScale = true;
        }
    });

    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isOnClick = true;
            int x = (int) e.getX();
            int y = (int) e.getY();

            Log.d("TAG", "row=" + row + ",column=" + column);

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    int tempX = (int) ((j * seatWidth + j * spacing) * getMatrixScaleX() + getTranslateX());
                    int maxTempX = (int) (tempX + seatWidth * getMatrixScaleX());

                    int tempY = (int) ((i * seatHeight + i * verSpacing) * getMatrixScaleY() + getTranslateY());
                    int maxTempY = (int) (tempY + seatHeight * getMatrixScaleY());


//                    if (seatChecker != null && seatChecker.isValidSeat(i, j) && !seatChecker.isSold(i, j)) {
                        if (x >= tempX && x <= maxTempX && y >= tempY && y <= maxTempY) {
                            int id = getID(i, j);
                            int index = isHave(id);
                            if (index >= 0) {
                                remove(index);
                                if (seatChecker != null) {
                                    seatChecker.unCheck(i, j);
                                }
                            } else {
                                if (selects.size() >= maxSelected) {
//                                    Toast.makeText(getContext(), "最多只能选择" + maxSelected + "个", Toast.LENGTH_SHORT).show();
                                    return super.onSingleTapConfirmed(e);
                                } else {
                                    addChooseSeat(i, j);
                                    if (seatChecker != null) {
                                        seatChecker.checked(i, j);
                                    }
                                }
                            }
//                            isNeedDrawSeatBitmap = true;
//                            isDrawOverviewBitmap = true;
                            float currentScaleY = getMatrixScaleY();

                            if (currentScaleY < 1.7) {
                                scaleX = x;
                                scaleY = y;
                                zoomAnimate(currentScaleY, 1.9f);
                            }

                            invalidate();
                            break;
//                        }
                    }



//                    if (x >= tempX && x <= maxTempX && y >= tempY && y <= maxTempY) {
//                        Log.d("TAG", isHave(i, j) + ":" + i + "," + j);
//                        if (isHave(i, j)) {
//                            remove(i, j);
//                        } else {
//                            list.add(new Point(i, j));
//                        }
//
////                        float currentScaleY = getMatrixScaleY();
////
////                        if (currentScaleY < 1.7) {
////                            scaleX = x;
////                            scaleY = y;
////                            zoomAnimate(currentScaleY, 1.9f);
////                        }
////
////                        invalidate();
////                        break;
//                    }
                }
            }

//            float currentScaleY = getMatrixScaleY();
//
//            if (currentScaleY < 1.7) {
//                scaleX = x;
//                scaleY = y;
//                zoomAnimate(currentScaleY, 1.9f);
//            }
//
//            invalidate();

//            return true;

            return super.onSingleTapConfirmed(e);
        }
    });


    private float dip2Px(float value) {
        return getResources().getDisplayMetrics().density * value;
    }

    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        int baseline = (int) ((bottom + top - fontMetrics.bottom - fontMetrics.top) / 2);
        return baseline;
    }

    float[] m = new float[9];

    private float getTranslateX() {
        matrix.getValues(m);
        return m[2];
    }

    private float getTranslateY() {
        matrix.getValues(m);
        return m[5];
    }

    private float getMatrixScaleX() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }

    private float getMatrixScaleY() {
        matrix.getValues(m);
        return m[4];
    }



    ArrayList<Integer> selects = new ArrayList<>();

    public ArrayList<String> getSelectedSeat(){
        ArrayList<String> results=new ArrayList<>();
        for(int i=0;i<this.row;i++){
            for(int j=0;j<this.column;j++){
                if(isHave(getID(i,j))>=0){
                    results.add(i+","+j);
                }
            }
        }
        return results;
    }


    private int isHave(Integer seat) {
        return Collections.binarySearch(selects, seat);
    }

    private void remove(int index) {
        selects.remove(index);
    }


//    private boolean isHave(int x, int y) {
//        if (list == null || list.isEmpty()) {
//            return false;
//        }
//        for (Point point : list) {
//            if (point.x == x && point.y == y) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private List<Point> delList = new ArrayList<>();
//    private void remove(int x, int y) {
//        Iterator<Point> it= list.iterator();
//        while (it.hasNext()) {
//            Point point = it.next();
//            if (point.x == x && point.y == y) {
//                delList.add(point);
//            }
//        }
//        list.removeAll(delList);
////        for (Point point : list) {
////            if (point.x == x && point.y == y) {
////                list.remove(point);
////            }
////        }
//    }

    private void drawText(Canvas canvas, int row, int column, float top, float left) {
        String txt = (row + 1) + "排";
        String txt1 = (column + 1) + "座";
        TextPaint txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float seatHeight = this.seatHeight * getMatrixScaleY();
        float seatWidth = this.seatWidth * getMatrixScaleX();
        txtPaint.setTextSize(seatHeight / 3);
        // 获取中间线
        float center = seatHeight / 2;
        float txtWidth = txtPaint.measureText(txt);
        float startX = left + seatWidth / 2 - txtWidth / 2;

        // 只绘制一行文字
        if (txt1 == null) {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + seatHeight), txtPaint);
        } else {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + center), txtPaint);
            canvas.drawText(txt1, startX, getBaseLine(txtPaint, top + center, top + center + seatHeight), txtPaint);
        }

        if (DBG) {
            Log.d("drawText:", "top:" + top);
        }
    }





    private void addChooseSeat(int row, int column) {
        int id = getID(row, column);
        for (int i = 0; i < selects.size(); i++) {
            int item = selects.get(i);
            if (id < item) {
                selects.add(i, id);
                return;
            }
        }

        selects.add(id);
    }




    public interface SeatChecker {
        /**
         * 是否可用座位
         *
         * @param row
         * @param column
         * @return
         */
        boolean isValidSeat(int row, int column);

        /**
         * 是否已售
         *
         * @param row
         * @param column
         * @return
         */
        boolean isSold(int row, int column);

        void checked(int row, int column);

        void unCheck(int row, int column);

        /**
         * 获取选中后座位上显示的文字
         * @param row
         * @param column
         * @return 返回2个元素的数组,第一个元素是第一行的文字,第二个元素是第二行文字,如果只返回一个元素则会绘制到座位图的中间位置
         */
        String[] checkedSeatTxt(int row,int column);

    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setMaxSelected(int maxSelected) {
        this.maxSelected = maxSelected;
    }

    public void setSeatChecker(SeatChecker seatChecker) {
        this.seatChecker = seatChecker;
        invalidate();
    }

    private int getRowNumber(int row){
        int result=row;
        if(seatChecker==null){
            return -1;
        }

        for(int i=0;i<row;i++){
            for (int j=0;j<column;j++){
                if(seatChecker.isValidSeat(i,j)){
                    break;
                }

                if(j==column-1){
                    if(i==row){
                        return -1;
                    }
                    result--;
                }
            }
        }
        return result;
    }

    private int getColumnNumber(int row,int column){
        int result=column;
        if(seatChecker==null){
            return -1;
        }

        for(int i=row;i<=row;i++){
            for (int j=0;j<column;j++){

                if(!seatChecker.isValidSeat(i,j)){
                    if(j==column){
                        return -1;
                    }
                    result--;
                }
            }
        }
        return result;
    }

}

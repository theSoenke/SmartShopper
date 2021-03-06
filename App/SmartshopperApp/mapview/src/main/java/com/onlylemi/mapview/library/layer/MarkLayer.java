package com.onlylemi.mapview.library.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.utils.MapMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MarkLayer
 *
 * @author: onlylemi
 */
public class MarkLayer extends MapBaseLayer {

    private List<PointF> marks;
    private List<String> marksName;
    private List<Integer> marksType;
    private MarkIsClickListener listener;

    private Map<Integer,Bitmap> bmpMarkTypes;
    private Map<Integer,Bitmap> bmpMarkTypesTouch;

    private float radiusMark;
    private boolean isClickMark = false;
    private int num = -1;

    private Paint paint;

    private boolean markTouchedHighLighted;

    public MarkLayer(MapView mapView) {
        super(mapView);
        marks = new ArrayList<>();
        marksName = new ArrayList<>();
        marksType = new ArrayList<>();
        bmpMarkTypes = new HashMap<>();
        bmpMarkTypesTouch = new HashMap<>();
        markTouchedHighLighted = true;
        initLayer();
    }

    private void initLayer() {
        radiusMark = setValue(10f);
        //bmpMark = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.mark);
        //bmpMarkTouch = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.mark_touch);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void highlightMarkTouch(boolean enable)
    {
        markTouchedHighLighted = enable;
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (marks != null) {
            if (!marks.isEmpty()) {
                float[] goal = mapView.convertMapXYToScreenXY(event.getX(), event.getY());
                for (int i = 0; i < marks.size(); i++) {
                    Bitmap bmpMark = bmpMarkTypes.get(marksType.get(i));
                    if (MapMath.getDistanceBetweenTwoPoints(goal[0], goal[1],
                            marks.get(i).x - bmpMark.getWidth() / 2, marks.get(i).y - bmpMark
                                    .getHeight() / 2) <= 50) {
                        num = i;
                        isClickMark = true;
                        break;
                    }
                }
            }

            if (listener != null && isClickMark) {
                listener.markIsClick(num);
                mapView.refresh();
                isClickMark = false;
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        if (isVisible && marks != null) {
            canvas.save();
            if (!marks.isEmpty()) {
                for (int i = 0; i < marks.size(); i++) {
                    PointF mark = marks.get(i);
                    Bitmap bmpMark = bmpMarkTypes.get(marksType.get(i));
                    Bitmap bmpMarkTouch = bmpMarkTypesTouch.get(marksType.get(i));
                    float[] goal = {mark.x, mark.y};
                    currentMatrix.mapPoints(goal);

                    paint.setColor(Color.BLACK);
                    paint.setTextSize(radiusMark);
                    //mark name
                    if (mapView.getCurrentZoom() > 1.0 && marksName != null
                            && marksName.size() == marks.size()) {
                        canvas.drawText(marksName.get(i), goal[0] - radiusMark, goal[1] -
                                radiusMark / 2, paint);
                    }
                    //mark ico
                    canvas.drawBitmap(bmpMark, goal[0] - bmpMark.getWidth() / 2,
                            goal[1] - bmpMark.getHeight() / 2, paint);
                    if (markTouchedHighLighted && i == num && isClickMark) {
                        canvas.drawBitmap(bmpMarkTouch, goal[0] - bmpMarkTouch.getWidth() / 2,
                                goal[1] - bmpMarkTouch.getHeight(), paint);
                    }
                }
            }
            canvas.restore();
        }
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<PointF> getMarks() {
        return marks;
    }

    public void setMarks(List<PointF> marks) {
        this.marks = marks;
    }

    public List<String> getMarksName() {
        return marksName;
    }

    public void setMarksName(List<String> marksName) {
        this.marksName = marksName;
    }

    public List<Integer> getMarksType() { return marksType; }

    public void setMarksType(List<Integer> marksType) {this.marksType = marksType; }

    public boolean isClickMark() {
        return isClickMark;
    }

    public void setMarkIsClickListener(MarkIsClickListener listener) {
        this.listener = listener;
    }

    public interface MarkIsClickListener {
        void markIsClick(int num);
    }

    public void addMarkType(int identifier, Bitmap bmpMark, Bitmap bmpMarkTouch)
    {
        bmpMarkTypes.put(identifier, bmpMark);
        bmpMarkTypesTouch.put(identifier, bmpMarkTouch);
    }
}

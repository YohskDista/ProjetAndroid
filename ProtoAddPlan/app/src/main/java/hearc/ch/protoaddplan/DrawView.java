package hearc.ch.protoaddplan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonardo.distasio on 30.10.2015.
 */
public class DrawView extends View
{
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private String picturePath;
    private List<Locator> locatorList;
    private int width;
    private int height;
    private int widthReal;
    private int heightReal;
    private int meterToPixelWidth;
    private int meterToPixelHeight;
    private MainActivity main;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(15.0f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        picturePath = "";
        locatorList = new ArrayList<Locator>();
    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(picturePath != "") canvas.drawBitmap(getResizedBitmap(width, height, picturePath), 0, 0, canvasPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    public Bitmap getResizedBitmap(int targetW, int targetH,  String imagePath)
    {
        int inWidth = 0;
        int inHeight = 0;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        inWidth = options.outWidth;
        inHeight = options.outHeight;

        options = new BitmapFactory.Options();
        options.inSampleSize = Math.max(inWidth/targetW, inHeight/targetH);
        Bitmap roughBitmap = BitmapFactory.decodeFile(picturePath, options);

        // calc exact destination size
        Matrix m = new Matrix();
        RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
        RectF outRect = new RectF(0, 0, targetW, targetH);
        m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
        float[] values = new float[9];
        m.getValues(values);

        // resize bitmap
        return Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        switch(event.getAction())
        {
            case MotionEvent.ACTION_UP:
                if(picturePath != "")
                {
                    Locator locErase = isLocatorHere(touchX, touchY);
                    if(locErase != null)
                    {
                        choiceOptionBeacon(locErase);
                        return true;
                    }
                    Locator locator = new Locator(touchX, touchY);
                    locator.setRealPX(touchX / meterToPixelWidth);
                    locator.setRealPY(touchY / meterToPixelHeight);
                    locatorList.add(locator);
                    locator.setIndex(locatorList.indexOf(locator));
                    canvasPaint.setColor(Color.BLUE);
                    drawCanvas.drawCircle(locator.getX(), locator.getY(), locator.getRadius(), canvasPaint);
                }
                break;
        }

        invalidate();
        return true;

    }

    private void repaintLocator()
    {
        for (Locator l:locatorList)
        {
            drawCanvas.drawCircle(l.getX(), l.getY(), l.getRadius(), canvasPaint);
        }
        invalidate();
    }

    private void choiceOptionBeacon(final Locator loc)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Gestion Beacon");
        builder.setMessage("Effectuez votre choix :");

        builder.setPositiveButton("Configuration", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                loc.setIndex(locatorList.indexOf(loc));
                main.callBeaconConfiguration(loc);
            }
        }).setNegativeButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                locatorList.remove(loc);
                repaintLocator();
            }
        });

        builder.show();
    }

    public Locator isLocatorHere(int x, int y)
    {
        for (Locator l:locatorList)
        {
            if(((l.getX() + meterToPixelWidth) > x && (l.getX() - meterToPixelWidth) < x)
                    && ((l.getY() + meterToPixelHeight) > y && (l.getY() - meterToPixelHeight) < y)) return l;
        }
        return null;
    }

    public void setPicturePath(String picturePath)
    {
        this.picturePath = picturePath;
        invalidate();
    }

    public void setDimension(int width, int height)
    {
        this.widthReal = width;
        this.heightReal = height;

        meterToPixelWidth = this.width / width;
        meterToPixelHeight = this.height / height;

        Toast.makeText(getContext(), meterToPixelWidth + "px/m, "+meterToPixelHeight+"px/m", Toast.LENGTH_SHORT).show();
    }

    public List<Locator> getListLocator()
    {
        return locatorList;
    }

    public int getWidthReal()
    {
        return widthReal;
    }

    public int getHeightReal()
    {
        return heightReal;
    }

    public void setMainActivity(MainActivity main)
    {
        this.main = main;
    }
}

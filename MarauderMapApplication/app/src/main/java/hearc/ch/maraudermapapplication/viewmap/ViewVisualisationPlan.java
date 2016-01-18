package hearc.ch.maraudermapapplication.viewmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hearc.ch.maraudermapapplication.tools.object.Locator;
import hearc.ch.maraudermapapplication.tools.object.PersonLocator;
import hearc.ch.maraudermapapplication.tools.object.Plan;

/**
 * Created by leonardo.distasio on 10.11.2015.
 */
public class ViewVisualisationPlan extends View
{
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Bitmap pictureBitmap;
    private List<Locator> locatorList;
    private Bitmap bitmapPlan;
    private int width;
    private int height;

    public ViewVisualisationPlan(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(15.0f);
        drawPaint.setTextSize(12.0f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        locatorList = new ArrayList<Locator>();
    }

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
        if(bitmapPlan != null) canvas.drawBitmap(getResizedBitmap(width, height, bitmapPlan), 0, 0, canvasPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    public Bitmap getResizedBitmap(int targetW, int targetH,  Bitmap bitmapPlan)
    {
        int inWidth = 0;
        int inHeight = 0;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        inWidth = options.outWidth;
        inHeight = options.outHeight;

        options = new BitmapFactory.Options();
        options.inSampleSize = Math.max(inWidth / targetW, inHeight / targetH);

        // calc exact destination size
        Matrix m = new Matrix();
        RectF inRect = new RectF(0, 0, bitmapPlan.getWidth(), bitmapPlan.getHeight());
        RectF outRect = new RectF(0, 0, targetW, targetH);
        m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
        float[] values = new float[9];
        m.getValues(values);

        // resize bitmap
        return Bitmap.createScaledBitmap(bitmapPlan, (int) (bitmapPlan.getWidth() * values[0]), (int) (bitmapPlan.getHeight() * values[4]), true);
    }

    public void setCircleToDraw(PersonLocator personLocator, List<PersonLocator> listPersonsLocators, Plan plan)
    {
        drawPaint.setStrokeWidth(15.0f);
        if(personLocator.getId_plan() == plan.getId()) {
            drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            drawPaint.setColor(Color.RED);
            drawCanvas.drawCircle(personLocator.getpX(), personLocator.getpY(), personLocator.getRadius(), drawPaint);
            drawPaint.setStrokeWidth(1.0f);
            drawCanvas.drawText(personLocator.getNom(), personLocator.getpX() - 35, personLocator.getpY() + 45, drawPaint);
        }

        for(PersonLocator pl : listPersonsLocators)
        {
            if(pl.getId() != personLocator.getId())
            {
                drawPaint.setStrokeWidth(15.0f);
                drawPaint.setColor(Color.BLUE);
                drawCanvas.drawCircle(pl.getpX(), pl.getpY(), pl.getRadius(), drawPaint);
                drawPaint.setStrokeWidth(1.0f);
                drawCanvas.drawText(pl.getNom(), pl.getpX() - 35, pl.getpY() + 45, drawPaint);
                drawPaint.setColor(paintColor);
            }
        }
        invalidate();
    }

    public void setImageUrl(String url)
    {
        new LoadImage().execute(url);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            try
            {
                bitmapPlan = BitmapFactory.decodeStream((InputStream)new URL(params[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmapPlan != null)
            {
                ViewVisualisationPlan.this.invalidate();
            }
        }
    }
}

package com.yun.balloons;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dell on 2016/7/22.
 */
public class BalloonsView extends View {
    private ArrayList<BalloonModel> balloonModels;
    private Handler handler;
    private int screenWidth, screenHeight;
    private final int DEFAULT_RADII = 50;
    private Paint paint;

    public BalloonsView(Context context) {
        super(context);
        init();
    }

    public BalloonsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BalloonsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        balloonModels = new ArrayList<BalloonModel>();
        handler = new Handler();
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        paint = new Paint();
        paint.setColor(getContext().getResources().getColor(R.color.balloon));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        handler.post(createBalloonRunnable);
        handler.post(animationRunnable);
    }


    private void createBalloon() {
        synchronized (balloonModels) {
            Random rand = new Random();
            int startX = rand.nextInt(screenWidth - DEFAULT_RADII * 2);
            BalloonModel balloonModel = new BalloonModel();
            balloonModel.x = startX + DEFAULT_RADII;
            balloonModel.y = screenHeight;
            balloonModel.radii = DEFAULT_RADII;
            balloonModels.add(balloonModel);
        }
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (balloonModels) {
                boolean is = false;
                for (BalloonModel balloonModel : balloonModels) {
                    if (balloonModel.y < screenHeight / 3) {
                        balloonModel.y = balloonModel.y - 5;
                    } else {
                        balloonModel.y = balloonModel.y - 3;
                    }

                }
            }
            invalidate();
            handler.postDelayed(this, 1);
        }
    };

    private Runnable createBalloonRunnable = new Runnable() {
        @Override
        public void run() {
            createBalloon();
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (balloonModels) {
            ArrayList<BalloonModel> temp = new ArrayList<BalloonModel>();
            for (BalloonModel balloonModel : balloonModels) {
                if (balloonModel.y > 0 - DEFAULT_RADII) {
                    if (balloonModel.isClick) {
                        RectF rectf = new RectF((float) (balloonModel.x - DEFAULT_RADII * (balloonModel.life / 5)), (float) (balloonModel.y - DEFAULT_RADII - DEFAULT_RADII * (balloonModel.life / 5)),
                                (float) (balloonModel.x + DEFAULT_RADII * 2 + DEFAULT_RADII * (balloonModel.life / 5)), (float) (balloonModel.y + DEFAULT_RADII + DEFAULT_RADII * (balloonModel.life / 5)));
                        canvas.drawOval(rectf, paint);
                        balloonModel.life = balloonModel.life + 1;
                        if (balloonModel.life >= 8) {
                            temp.add(balloonModel);
                        }
                    } else {
                        RectF rectf = new RectF(balloonModel.x, balloonModel.y - DEFAULT_RADII, balloonModel.x + DEFAULT_RADII * 2, balloonModel.y + DEFAULT_RADII);
                        canvas.drawOval(rectf, paint);
                    }

                } else {
                    temp.add(balloonModel);
                }
            }
            balloonModels.removeAll(temp);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                synchronized (balloonModels) {
                    float x = event.getX();
                    float y = event.getY();
                    for (BalloonModel balloonModel : balloonModels) {
                        if (x > balloonModel.x && x < balloonModel.x + DEFAULT_RADII * 2 && y > balloonModel.y && y < balloonModel.y + DEFAULT_RADII * 2) {
                            balloonModel.isClick = true;
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}

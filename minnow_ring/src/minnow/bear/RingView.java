package minnow.bear;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.widget.TextView;
import android.util.Log;


class RingView extends SurfaceView implements SurfaceHolder.Callback, 
    OnTouchListener, OnGestureListener{

    private static final String TAG = "RingView";
    private static int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 200;

    private Ring ring0;
    private Ring ring1;

    class RingThread extends Thread {


        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;


        private boolean mRun = false;

        /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int mMode;

        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;

        /** Used to figure out elapsed time between frames */
        private long mLastTime;

        private SurfaceHolder mSurfaceHolder;
        
        public RingThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            mSurfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean b) {
            mRun = b;
        }

        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
                Log.w(this.getClass().getName(), mCanvasWidth + ":" + mCanvasHeight);
                FLING_MIN_DISTANCE = width/6;
            }
        }

        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
            }
        }

        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
            }
            return map;
        }

        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
            }
        }

        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
        }
 

        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                mMode = mode;
            }
        }
        
        public void doStart() {
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING) updatePhysics();
                        Log.v(TAG, "going to doDraw");
                        doDraw(c);
                        if (System.currentTimeMillis() > mLastTime){
                            mLastTime = System.currentTimeMillis() + 100;
                        }
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private void doDraw(Canvas canvas) {
            //Log.w(TAG, "doDraw");
            ring0.clear();
            ring1.clear();
            ring0.draw(canvas); 
            ring1.draw(canvas); 
        }

        private void updatePhysics() {
        }
        
    }

    private SurfaceHolder holder;
    private RingThread thread;
    private GestureDetector mGestureDetector;

    public RingView(Context context) throws InterruptedException {
            super(context);

            // register our interest in hearing about changes to our surface
            SurfaceHolder holder = getHolder();
            holder.addCallback(this);

            mGestureDetector = new GestureDetector(this);
            setOnTouchListener(this);
            setLongClickable(true);

            BeadSlot bs = new BeadSlot(90, 200, 1, 0);
            ring0 = new Ring(bs, Math.PI/6, 12, 2);
            ring1 = new Ring(bs, -Math.PI/6, 12, 1);
            ring0.setCross(ring1);


            // create thread only; it's started in surfaceCreated()
            thread = new RingThread(holder, context, new Handler() {
                @Override
                public void handleMessage(Message m) {
                }
            });

            setFocusable(true);
        }

    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        Log.v(TAG, "surfaceCreated");
        thread.setRunning(true);
        thread.start();
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public RingThread getThread() {
        return thread;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.v(TAG, "onTouch");
        return mGestureDetector.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent e) {
        Log.v(TAG, "onDown");
        return false;
    }


    public void onLongPress(MotionEvent e) {
        Log.v(TAG, "onLongPress");
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        Log.v(TAG, "onScroll");
        return false;
    }

    public void onShowPress(MotionEvent e) {
        Log.v(TAG, "onShowPress");
    }

    public boolean onSingleTapUp(MotionEvent e) {
        Log.v(TAG, "onSingleTapUp");
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
        float velocityY) {
        Log.v(TAG, "onFling");
        float vx = Math.abs(velocityX);
        float vy = Math.abs(velocityY);
        float max_v = vx > vy ? vx : vy;
        if (max_v < FLING_MIN_VELOCITY) return false;
        int x1 = new Float(e1.getX()).intValue();
        int y1 = new Float(e1.getY()).intValue();
        int x2 = new Float(e2.getX()).intValue();
        int y2 = new Float(e2.getY()).intValue();
        int d1_2 = (x1-x2)*(x1-x2)+ (y1-y2)*(y1-y2);
        if (d1_2 < FLING_MIN_DISTANCE*FLING_MIN_DISTANCE) return false;
        //ring0.rotate(true, 1, false);
        ring0.rotate(true, 1, true);
        //ring1.rotate(false, 1, false);
        //ring1.rotate(false, 1, true);
        return true;
    }

}

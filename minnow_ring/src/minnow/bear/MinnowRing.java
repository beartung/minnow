package minnow.bear;

import minnow.bear.RingView.RingThread;

import android.app.Activity;

import android.view.Window;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Bundle;

import android.util.Log;

public class MinnowRing extends Activity
{
    private static final int MENU_PAUSE = 0;

    private static final int MENU_RESUME = 1;

    private static final int MENU_START = 2;

    private static final int MENU_STOP = 3;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_START:
                rt.doStart();
                return true;
            case MENU_STOP:
                rt.setState(rt.STATE_LOSE);
                return true;
            case MENU_PAUSE:
                rt.pause();
                return true;
            case MENU_RESUME:
                rt.unpause();
                return true;
        }
        return false;
    }


    private RingView rv;

    private RingThread rt;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        rv = new RingView(this);
        setContentView(rv);

        rt = rv.getThread();

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            //rt.setState(RingThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            rt.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        rv.getThread().pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        rt.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }

}

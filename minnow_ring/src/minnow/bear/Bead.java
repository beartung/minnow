package minnow.bear;

import android.graphics.drawable.*;
import android.graphics.*;
import android.graphics.RadialGradient;
import android.graphics.drawable.shapes.*;
import android.graphics.Shader.TileMode;
import android.util.Log;
import java.lang.Math;
import java.lang.Double;
import java.lang.Float;

public class Bead
{
    private static final String TAG = "[BEAR] Bead";

    //public static final int s_r = 5;
    public static final int s_offset = 5;

    public static final int RED = 0xFFFF0000;
    public static final int L_RED = 0xEEFFCBCB;
    public static final int GREEN = 0xFF00FF00;
    public static final int L_GREEN = 0xFFCBFFCB;
    public static final int BLUE = 0xFF0000FF;
    public static final int L_BLUE = 0xFFCBCBFF;
    public static final int LIGHT = 0x55EEEEEE;
    public static final int GRAY  = 0x88DDDDDD;
    public static final int BLACK = 0x88000000;

    public static final int[] colors = {BLUE, RED, GREEN};
    public static final int[] light_colors = {L_BLUE, L_RED, L_GREEN};
    public static final int COLOR_NUM = 4;
    public static final Paint paint = new Paint();
    static {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    } 


    public int x;
    public int y;
    public int r;
    public int color;
    public double angle;
    private RectF ball;
    //private RectF lightspot;
    public Bead(){
        x = y = r = color = 0;
        ball = new RectF(x-r, y-r, x+r, y+r);
    }

    public void setProps(BeadSlot bs, Ring rr, double angle){
        x = bs.x;
        y = bs.y;
        r = bs.r;
        color = bs.color;
        double a = angle;
        if (a != 0){
            a = bs.angle + angle;
            if (a > Math.PI*2) a -= Math.PI*2;
            if (a < 0) a += Math.PI*2;
            x = new Double(rr.x - rr.r*(Math.cos(a))).intValue();
            y = new Double(rr.y + rr.r*(Math.sin(a))).intValue();
        }
        ball.set(x-r, y-r, x+r, y+r); 
        //int xx = x - s_offset;
        //int yy = y - s_offset;
        //lightspot = new RectF(xx-s_r, yy-s_r, xx+s_r, yy+s_r);
    }

    public void draw(Canvas canvas){
        RadialGradient rg = new RadialGradient(x-s_offset, y-s_offset, r, light_colors[color], colors[color], TileMode.MIRROR);
        paint.setShader(rg);
        canvas.drawArc(ball, 0, 360, true, paint);
        //canvas.drawArc(lightspot, 0, 360, true, paint);
    }

};

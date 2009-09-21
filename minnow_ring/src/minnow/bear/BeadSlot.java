package minnow.bear;
import android.graphics.drawable.*;
import android.graphics.*;
import android.graphics.drawable.shapes.*;
import android.util.Log;
import java.lang.Math;
import java.lang.Double;
import java.lang.Float;

public class BeadSlot
{
    private static final String TAG = "[BeadSlot] ";
    public static final int r = 20;

    public final int x;
    public final int y;
    public final double angle;
    public int color;
    public boolean drawed;

    public BeadSlot(int x, int y, int c, double angle){
        this.x = x;
        this.y = y;
        this.color = c;
        this.drawed = false;
        this.angle = angle;
    }

    public boolean equals(BeadSlot bs){
       return (Math.abs(x - bs.x) < 2) && (Math.abs(y - bs.y) < 2);
    }

};

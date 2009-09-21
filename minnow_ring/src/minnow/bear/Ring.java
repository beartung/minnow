package minnow.bear;

import android.graphics.drawable.*;
import android.graphics.*;
import android.graphics.drawable.shapes.*;
import android.util.Log;
import java.lang.Math;
import java.lang.Double;
import java.lang.Float;
import java.util.Hashtable;
import java.util.Enumeration;

public class Ring extends ShapeDrawable
{
    private static final String TAG = "[Ring] ";
    
    public final int x;
    public final int y;
    public final int r;
    public final int color;
    public final double each_angle;
    public final int bead_num;
    public BeadSlot[] slots;
    public Bead[] beads;

    private boolean start_animation = false;
    private double animation_angle = 0;
    private boolean clockwise = false;
    private int step = 0;
    private double end_angle = 0;

    public Ring(BeadSlot start, double angle, int bead_num, int color){
        each_angle = Math.PI*2/bead_num;
        r = new Double(start.r/Math.sin(each_angle/2)).intValue();
        x = (new Double(r*Math.cos(angle))).intValue()+start.x;
        y = start.y - (new Double(r*Math.sin(angle))).intValue();
        this.color = color;
        this.bead_num = bead_num;

        slots = new BeadSlot[bead_num];
        beads = new Bead[bead_num];
        slots[0] = start;
        beads[0] = new Bead();
        beads[0].setProps(slots[0], this, 0);
        double a = angle;
        int xx = 0;
        int yy = 0;
        for (int i = 1; i < bead_num; i++){
            a += each_angle;
            if (a > Math.PI*2) a -= Math.PI*2;
            if (a < 0) a += Math.PI*2;
            xx = new Double(x - r*(Math.cos(a))).intValue();
            yy = new Double(y + r*(Math.sin(a))).intValue();
            slots[i] = new BeadSlot(xx, yy, color, a);
            beads[i] = new Bead();
            beads[i].setProps(slots[i], this, 0);
        }

        for (int i = 0; i < bead_num; i++){
            Log.v(TAG, "a: "+beads[i].angle); 
        }
    }

    public void setCross(Ring r){
        for (int i = 0; i < bead_num; i++){
            for (int j = 0; j < r.bead_num; j++){
                if (slots[i].equals(r.slots[j])){
                    slots[i] = r.slots[j];
                }
            }
        }
    }

    private void move(){
        Log.v(TAG, "angle:"+animation_angle+" end_angle:"+end_angle);
        int[] save_color = new int[step];
        if (clockwise){
            for (int i = 0; i < step; i++){
                save_color[i] = slots[i].color; 
            }
            for (int i = 0; i < bead_num - step; i++){
                slots[i].color = slots[i+step].color; 
            }
            for (int i = 0; i < step; i++){
                slots[i + bead_num - step].color = save_color[i]; 
            }
        }else{
            for (int i = 0; i < step; i++){
                save_color[i] = slots[bead_num - step + i].color; 
            }
            for (int i = bead_num - 1; i >= step; i--){
                slots[i].color = slots[i-step].color; 
            }
            for (int i = step - 1; i >= 0; i--){
                slots[i].color = save_color[i]; 
            }
        
        } 
    }

    public void rotate(boolean clockwise, int step, boolean animation){
        Log.v(TAG, "clockwise: "+clockwise+" step: "+step);

        clockwise = clockwise;
        step = step;
        end_angle = each_angle*step;
        if (animation){
            start_animation = true;
        }else{
            end_angle = 0;
            move();
        }
    }

    public void clear(){
        for (int i = 0; i < bead_num; i++){
            slots[i].drawed = false;
        }
    }

    public void draw(Canvas canvas){
        double angle = 0;
        if (start_animation && Math.abs(end_angle - animation_angle) < 0.1){
                Log.v(TAG, "start_animatin:"+start_animation+":"+animation_angle+" end_angle:"+end_angle);
                //start_animation = false; 
                animation_angle = 0;
                move();
        }
        if (start_animation){
            if (!clockwise){
                animation_angle += 0.1;
            }else{
                animation_angle -= 0.1;
            }
            Log.v(TAG, "start_animatin:"+start_animation+":"+animation_angle+" end_angle:"+end_angle);
        }
        angle = animation_angle;
        for (int i = 0; i < bead_num; i++){
            if (!slots[i].drawed){
                beads[i].setProps(slots[i], this, angle);
                beads[i].draw(canvas);
                slots[i].drawed = true;
            }
        }
    }

    public boolean check(){
        for (int i = 0; i < bead_num; i++){
            if (slots[i].color != color){
                return false;    
            }
        }
        return true;
    }

};
